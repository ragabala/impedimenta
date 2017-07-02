/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A cache, implemented with weak references.
 *
 * This cache is thread-safe. The reader and writer methods are implemented with
 * a read-write lock. Beware that this cache is *not* implemented with the
 * <code>volatile</code> keyword. If multiple threads concurrently read from and
 * write to a cache object and each thread must have access to up-to-date
 * values, the <code>volatile</code> keyword should be used when creating a
 * cache object.
 *
 * If the cache's size is limited, then the age of each key-value pair is
 * tracked. Each time {@link storeValue} or {@link getValue} is called, the age
 * of each existing key-value pair is incremented. If an insertion causes the
 * cache to overflow, the oldest key-value pair is dropped from the cache. Reads
 * do affect the age of key-value pairs. Compared to an implementation where
 * only writes affect the age of key-value pairs, this implementation can lead
 * to more desireable retention decisions. However, it allows for significantly
 * lower throughput in multi-threaded read-heavy use cases.
 */
public class Cache<K, V> {
    private final Map<K, SoftReference<V>> cache;
    private final Integer size; // null indicates unlimited size
    private final List<K> recentlyUsed; // only set if size is non-null

    /** Create a cache of unlimited size. */
    public Cache() {
        this(null);
    }

    /**
     * Create a cache of limited size.
     *
     * @param size The number of key-value pairs the cache may hold.
     */
    public Cache(Integer size) {
        this.size = size;
        recentlyUsed = new ArrayList<K>();
        cache = new HashMap<>();
    }

    /**
     * Tell whether a value is present in the cache.
     *
     * @param key When executed, this method will search for the value
     * corresponding to this key.
     * @return Whether the searched-for value is present in the cache.
     */
    // IMO, `hasValue` is a better name for this method.
    public synchronized Boolean hasKey(K key) {
        if (getValue(key) == null) {
            return Boolean.valueOf(false);
        } else {
            return Boolean.valueOf(true);
        }
    }

    /**
     * Store a key/value pair.
     *
     * If the given key is already present, replace the corresponding value.
     *
     * @param key One half of a key-value pair to store in the cache.
     * @param value One half of a key-value pair to store in the cache.
     */
    public synchronized void storeValue(K key, V value) {
        cache.put(key, new SoftReference<V>(value));
        if (size == null) {
            return;
        }
        Integer index = recentlyUsed.indexOf(key);
        if (index == -1) {
            // This key-value pair is new to the cache. Insert the key at the
            // head of the recently used list, and if the cache is overflowing,
            // drop the key-value pair at the tail of the recently used list.
            recentlyUsed.add(0, key);
            if (recentlyUsed.size() > size) {
                cache.remove(recentlyUsed.get(size));
                recentlyUsed.remove((int)size); // Remove by index, NOT value!
            }
        } else {
            // This key-value pair is already in the cache. Move the key to the
            // head of the recently used list.
            recentlyUsed.remove((int)index); // Remove by index, NOT value!
            recentlyUsed.add(0, key);
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
        // If this cache is bounded and if the key-value pair being fetched is
        // in the cache, let the key-value pair being fetched be listed as the
        // most recently used.
        if (size != null) {
            Integer index = recentlyUsed.indexOf(key);
            if (!index.equals(Integer.valueOf(-1))) {
                recentlyUsed.remove((int)index); // Remove by index, NOT value!
                recentlyUsed.add(0, key);
            }
        }
        SoftReference<V> ref = cache.get(key);
        if (ref == null) {
            return null;
        } else {
            return ref.get(); // Null if reference has died.
        }
    }
}
