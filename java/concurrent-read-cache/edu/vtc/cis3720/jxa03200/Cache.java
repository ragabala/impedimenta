/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
 * tracked. Each time {@link storeValue} is called, the age of each existing
 * key-value pair is incremented. If an insertion causes the cache to overflow,
 * the oldest key-value pair is dropped from the cache. Reads do not affect the
 * age of key-value pairs. Compared to an implementation where both reads and
 * writes affect the age of key-value pairs, this implementation can lead to
 * more undesireable retention decisions. However, it allows for significantly
 * higher throughput in multi-threaded read-heavy use cases.
 */
public class Cache<K, V> {
    private final Map<K, SoftReference<V>> cache;
    private final Integer size; // null indicates unlimited size
    private final List<K> recentlyUsed; // only set if size is non-null

    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;

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

        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    /**
     * Tell whether a value is present in the cache.
     *
     * @param key When executed, this method will search for the value
     * corresponding to this key.
     * @return Whether the searched-for value is present in the cache.
     */
    // IMO, `hasValue` is a better name for this method.
    public boolean hasKey(K key) {
        readLock.lock();
        try {
            return getValue(key) != null;
        } finally {
            readLock.unlock();
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
    public void storeValue(K key, V value) {
        writeLock.lock();
        try {
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
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Return a value from the cache.
     *
     * @param key When executed, this method will return the value corresponding
     * to this skey.
     * @return The value, if it is present in the cache. Otherwise, null.
     */
    public V getValue(K key) {
        readLock.lock();
        try {
            SoftReference<V> ref = cache.get(key);
            if (ref == null) {
                return null;
            } else {
                return ref.get(); // Null if reference has died.
            }
        } finally {
            readLock.unlock();
        }
    }
}
