/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * A cache, implemented with weak references and functional interfaces.
 *
 * Cache objects are thread-safe. Beware that this class is *not* implemented
 * with the <code>volatile</code> keyword. If multiple threads concurrently read
 * from and write to a cache object and each thread must have access to
 * up-to-date values, the <code>volatile</code> keyword should be used when
 * creating that cache object.
 *
 * By default, the size of a cache object is limited only by system resources,
 * such as the amount of memory allocated to the JVM. The size of a cache object
 * can be further limited by setting a <code>maxCost</code>. Each time an object
 * is inserted into the cache, a cost for that object is calculated, and an
 * internal counter is incremented by that amount. Conversely, each time an
 * object is evicted from the cache, the internal counter is decremented by that
 * object's cost. Should the total cost of the objects in the cache exceed
 * <code>maxCost</code>, objects are evicted from the cache until the total cost
 * of objects in the cache is less than or equal to <code>maxCost</code>.
 *
 * Two relevant questions arise:
 *
 * * What is the cost of each object in the cache?
 * * When the cache becomes too expensive, what is the procedure for selecting
 *   objects to remove from the cache?
 *
 * Each object has an integer cost. Whenever an object is inserted into the
 * cache, an "appraiser" function is called on the object and returns a cost for
 * that object. This function is only called on each object at insertion time,
 * and never at a later time. (This is necessary because cached objects are not
 * guaranteed to be available for reappraisals, due to the nature of soft
 * references.) As a result, it is recommended that custom appraiser functions
 * calculate costs based on immutable object attributes. The default appraiser
 * assigns every object a cost of 1.
 *
 * Objects are not removed based on their cost. Instead, the removal procedure
 * is as follows:
 *
 * 1. Consider each object that is not protected by a "protector" function.
 * 2. Calculate a worth for each object with an "evictor" function.
 * 3. Evict objects from the cache in order from least worthy to most worthy.
 *    Stop when the cache is no longer too expensive, or when there are no more
 *    candidates for eviction.
 *
 * The default protector lets every object be considered for removal. The
 * default evictor assigns a lower worth to objects for which references have
 * died.
 */
public class Cache<K, V> {
    private final Map<K, SoftReference<V>> cache;
    private final Map<K, Integer> costs;
    private final Integer maxCost; // null indicates unlimited size
    private ToIntFunction<V> appraiser;
    private ToIntFunction<SoftReference<V>> evictor;
    private Predicate<SoftReference<V>> protector;

    /** Create an unbounded cache. */
    public Cache() {
        this(null);
    }

    /**
     * Create a bounded cache.
     *
     * @param maxCost The maximum allowable total cost of the values in this
     * cache.
     */
    public Cache(Integer maxCost) {
        this.cache = new HashMap<>();
        this.costs = new HashMap<>();
        this.maxCost = maxCost;
        this.appraiser = val -> 1;
        this.evictor = ref -> {
            V val = ref.get();
            if (val == null) {
                return 0;
            } else {
                return 1;
            }
        };
        this.protector = ref -> false;
    }

    /**
     * Set an appraiser function.
     *
     * @param appraiser A function which, when given an object from the cache,
     * will return a cost for that object.
     */
    public synchronized void setAppraiser(ToIntFunction<V> appraiser) {
        this.appraiser = appraiser;
    }

    /**
     * Set an evictor function.
     *
     * @param evictor A function which, when given a reference to an object from
     * the cache, will return a worth for that object.
     */
    public synchronized void setEvictor(
            ToIntFunction<SoftReference<V>> evictor) {
        this.evictor = evictor;
    }

    /**
     * Set an protector function.
     *
     * @param protector A function which, when given a reference to an object
     * from the cache, will state whether that object should be protected from
     * eviction. (To protect an object, this function should return true.)
     */
    public synchronized void setProtector(
            Predicate<SoftReference<V>> protector) {
        this.protector = protector;
    }

    /**
     * Tell whether a value is present in the cache.
     *
     * @param key When executed, this method will search for the value
     * corresponding to this key.
     * @return Whether the searched-for value is present in the cache.
     */
    public synchronized boolean hasValue(K key) {
        return getValue(key) != null;
    }

    /**
     * Store an object in the cache.
     *
     * If the given key is already present, replace the corresponding value. If
     * this insertion makes the cache too expensive, evict objects as necessary.
     *
     * @param key One half of a key-value pair to store in the cache.
     * @param value One half of a key-value pair to store in the cache.
     */
    public synchronized void storeValue(K key, V value) {
        cache.put(key, new SoftReference<V>(value));

        // If the cache is unbounded, skip all accounting work.
        if (maxCost == null) {
            return;
        }

        // Insert the new value.
        costs.put(key, Integer.valueOf(appraiser.applyAsInt(value)));
        Integer totalCost = costs
            .values()
            .stream()
            .reduce(Integer.valueOf(0), (x, y) -> x + y);
        if (totalCost <= maxCost) {
            return;
        }

        // Evict objects from the cache.
        List<K> evictees = cache
            .entrySet()
            .stream()
            .filter(entry -> !protector.test(entry.getValue()))
            .map(entry -> new AbstractMap.SimpleImmutableEntry<K, Integer>(
                entry.getKey(),
                Integer.valueOf(evictor.applyAsInt(entry.getValue()))
            ))
            .sorted((assessment1, assessment2) -> Integer.compare(
                assessment1.getValue(),
                assessment2.getValue()
            ))
            .map(assessment -> assessment.getKey())
            .collect(Collectors.toList());
        for (K evictee : evictees) {
            cache.remove(evictee);
            totalCost -= costs.get(evictee);
            costs.remove(evictee);
            if (totalCost <= maxCost) {
                break;
            }
        }
    }

    /**
     * Return a value from the cache.
     *
     * @param key When executed, this method will return the value corresponding
     * to this skey.
     * @return The value, if it is present in the cache. Otherwise, null.
     */
    public synchronized V getValue(K key) {
        SoftReference<V> ref = cache.get(key);

        // Is the key-value pair absent from the cache?
        if (ref == null) {
            return null;
        }

        // Is the reference to the value dead?
        V value = ref.get();
        if (value == null) {
            cache.remove(key);
            costs.remove(key);
        }
        return value;
    }
}
