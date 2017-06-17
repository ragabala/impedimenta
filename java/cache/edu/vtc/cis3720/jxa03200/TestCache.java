/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.Runtime;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Unit tests for class <code>Cache</code>. */
public class TestCache {
    /** Call the getter methods on an empty unbounded cache. */
    @Test
    public void testGetEmptyUnboundedCache() {
        Cache<Integer, Integer> cache = new Cache<>();
        doTestGetEmptyCache(cache);
    }

    /** Call the getter methods on an empty bounded cache. */
    @Test
    public void testGetEmptyBoundedCache() {
        Cache<Integer, Integer> cache = new Cache<>(10);
        doTestGetEmptyCache(cache);
    }

    /**
     * Implement logic common to several test methods.
     *
     * Implement logic for:
     *
     * * {@link testGetEmptyUnboundedCache}
     * * {@link testGetEmptyBoundedCache}
     */
    private void doTestGetEmptyCache(Cache<Integer, Integer> cache) {
        assertFalse(cache.hasKey(0));
    }

    /** Call the getter methods on a populated unbounded cache. */
    @Test
    public void testGetPopulatedUnboundedCache() {
        Cache<Integer, Integer> cache = new Cache<>();
        doTestGetPopulatedCache(cache);
    }

    /** Call the getter methods on a populated bounded cache. */
    @Test
    public void testGetPopulatedBoundedCache() {
        Cache<Integer, Integer> cache = new Cache<>(10);
        doTestGetPopulatedCache(cache);
    }

    /**
     * Implement logic common to several test methods.
     *
     * Implement logic for:
     *
     * * {@link testGetPopulatedUnboundedCache}
     * * {@link testGetPopulatedBoundedCache}
     */
    private void doTestGetPopulatedCache(Cache<Integer, Integer> cache) {
        cache.storeValue(0, 10);
        assertTrue(cache.hasKey(0));
        assertEquals(cache.getValue(0), Integer.valueOf(10));

        cache.storeValue(0, 11);
        assertTrue(cache.hasKey(0));
        assertEquals(cache.getValue(0), Integer.valueOf(11));
    }

    /**
     * Insert a series of key/value pairs into a small cache.
     *
     * Assert only the most recently inserted pairs are present.
     */
    @Test
    public void testEvictOldItems() {
        Integer cacheSize = 2;
        Integer numValues = 10;
        Cache<Integer, Integer> cache = new Cache<>(cacheSize);
        for (Integer i = 0; i < numValues; i++) {
            cache.storeValue(i, i);
        }
        for (Integer i = 0; i < numValues - cacheSize; i++) {
            assertFalse(cache.hasKey(i));
        }
        for (Integer i = numValues - cacheSize; i < numValues; i++) {
            assertTrue(cache.hasKey(i));
        }
    }

    /**
     * Repeatedly insert the same value into a small cache.
     *
     * Assert these insertions only consume one slot in the cache.
     */
    @Test
    public void testInsertSameValue() {
        Cache<Integer, Integer> cache = new Cache<>(2);
        cache.storeValue(0, 0);
        for (Integer i = 0; i < 10; i++) {
            cache.storeValue(1, 1);
        }
        assertTrue(cache.hasKey(0));
        assertTrue(cache.hasKey(1));
    }

    /**
     * Alternately write to and read from a cache.
     *
     * Assert that reads affect the freshness of the fetched value.
     */
    @Test
    public void testReadUpdatesAge() {
        Cache<Integer, Integer> cache = new Cache<>(2);
        for (Integer i = 0; i < 10; i++) {
            cache.storeValue(i, i);
            cache.getValue(0);
        }
        assertTrue(cache.hasKey(0));
        assertTrue(cache.hasKey(9));
    }

    /**
     * Insert a series of values into a cache from multiple threads.
     *
     * Assert that one of the final key-value pairs from one of the threads ends
     * up in the cache. This test doesn't prove very much. It simply attempts to
     * trigger a <code>ConcurrentModificationException</code>.

     * @throws InterruptedException If the threaded portion of this test is
     * interrupted.
     */
    @Test
    public void testThreadedInsertions() throws InterruptedException {
        // define runnables
        Cache<Integer, Integer> cache = new Cache<>();
        Integer limit = new Integer(1024); // This value is arbitrary.
        Runnable insertIntegers = new Runnable() {
            @Override
            public void run() {
                for (Integer i = new Integer(0); i < limit; i++) {
                    cache.storeValue(0, i);
                }
            }
        };

        // execute runnables
        Integer cores = Integer.valueOf(
            Runtime.getRuntime().availableProcessors()
        );
        ArrayList<Thread> threads = new ArrayList<>();
        for (Integer i = new Integer(0); i < cores; i++) {
            threads.add(new Thread(insertIntegers));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        // test cache
        assertTrue(cache.hasKey(0));
        assertEquals(new Integer(limit - 1), cache.getValue(0));
    }
}
