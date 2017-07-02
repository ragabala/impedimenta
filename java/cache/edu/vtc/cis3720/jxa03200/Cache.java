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
    public synchronized boolean hasKey(K key) {
        return getValue(key) != null;
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
        SoftReference<V> ref = cache.get(key);
        cache.put(key, new SoftReference<V>(value));

        // Is the cache unbounded?
        if (size == null) {
            return;
        }

        // Was the key already present in the cache?
        if (ref != null) {
            recentlyUsed.remove(key);
        }
        recentlyUsed.add(0, key);

        // Is the cache now overflowing?
        if (recentlyUsed.size() > size) {
            cache.remove(recentlyUsed.get(size));
            recentlyUsed.remove((int)size); // Remove by index, not value.
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
        recentlyUsed.remove(key);
        if (value == null) {
            cache.remove(key);
        } else {
            recentlyUsed.add(0, key);
        }
        return value;
    }
}
