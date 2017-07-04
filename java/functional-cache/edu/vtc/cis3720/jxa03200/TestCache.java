/** Author: Jeremy Audet */
package edu.vtc.cis3720.jxa03200;

import java.lang.Runtime;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Unit tests for class <code>Cache</code>. */
public class TestCache {
    /** Perform basic operations on a <code>Cache()</code>. */
    @Test
    public void testBasicsV1() {
        Cache<String, String> cache = new Cache<>();
        doTestBasics(cache);
    }

    /** Perform basic operations on a <code>Cache(maxCost)</code>. */
    @Test
    public void testBasicsV2() {
        Cache<String, String> cache = new Cache<>(10);
        doTestBasics(cache);
    }


    /**
     * Implement logic common to several test methods.
     *
     * Implement logic for:
     *
     * * {@link testBasicsV1}
     * * {@link testBasicsV2}
     * * {@link testBasicsV3}
     */
    private void doTestBasics(Cache<String, String> cache) {
        assertFalse(cache.hasValue("k1"));

        cache.storeValue("k1", "v1");
        assertTrue(cache.hasValue("k1"));
        assertEquals(cache.getValue("k1"), "v1");

        cache.storeValue("k1", "v2");
        assertTrue(cache.hasValue("k1"));
        assertEquals(cache.getValue("k1"), "v2");
    }

    /** Test whether the cache evicts objects when it becomes too expensive. */
    @Test
    public void testMaxCost() {
        final int maxCost = 3;
        Cache<Integer, Integer> cache = new Cache<>(maxCost);
        for (int i = 0; i < 10; i += 1) {
            cache.storeValue(i, i);
        }
        int numStored = 0;
        for (int i = 0; i < 10; i += 1) {
            if (cache.hasValue(i)) {
                numStored += 1;
            }
        }
        assertEquals(numStored, maxCost);
    }

    /**
     * Test whether an appraiser function can be specified.
     *
     * Create a cache with a max cost of 3 and with an appraiser that returns a
     * cost of 2 for each object inserted into the cache. Insert several objects
     * into the cache. Assert that only one of the inserted objects is kept in
     * the cache.
     */
    @Test
    public void testAppraiser() {
        Cache<Integer, Integer> cache = new Cache<>(3);
        cache.setAppraiser(val -> 2);
        for (int i = 0; i < 10; i += 1) {
            cache.storeValue(i, i);
        }
        int numStored = Stream
            .iterate(0, n -> n += 1)
            .limit(10)
            .mapToInt(i -> {if (cache.hasValue(i)) {return 1;} else {return 0;}})
            .sum();
        assertEquals(numStored, 1);
    }

    /**
     * Test whether an evictor function can be specified.
     *
     * Create a bounded cache with an evictor function. Insert several objects
     * into the cache. Assert that the correct objects are evicted.
     */
    @Test
    public void testEvictor() {
        // A Cache<Integer, Integer> could be used. But I want the evictor
        // function to have to produce an int from some other type. From a
        // black-box perspective, this helps prove that the implementation of
        // Cache isn't doing something stupid like passing keys to the evictor
        // or simply avoiding the evictor altogether.
        Cache<Integer, String> cache;

        // This test leaves the possibility of the cache having FIFO behaviour.
        cache = helpTestEvictor();
        for (int i = 0; i < 5; i += 1) {
            cache.storeValue(i, new String(new char[i]).replace("\0", "x"));
        }
        assertFalse(cache.hasValue(0));
        assertFalse(cache.hasValue(1));
        assertFalse(cache.hasValue(2));
        assertTrue(cache.hasValue(3));
        assertTrue(cache.hasValue(4));

        // This test proves that the cache isn't using FIFO behaviour.
        cache = helpTestEvictor();
        for (int i = 4; i >= 0; i -= 1) {
            cache.storeValue(i, new String(new char[i]).replace("\0", "x"));
        }
        assertFalse(cache.hasValue(0));
        assertFalse(cache.hasValue(1));
        assertFalse(cache.hasValue(2));
        assertTrue(cache.hasValue(3));
        assertTrue(cache.hasValue(4));
    }

    /** Return a cache for use by {@link testEvictor}. */
    private Cache<Integer, String> helpTestEvictor() {
        Cache<Integer, String> cache = new Cache<>(2);
        cache.setEvictor(ref -> {
            String string = ref.get();
            if (string == null) {
                return -1;
            } else {
                return string.length();
            }
        });
        return cache;
    }

    /**
     * Test whether a protector function can be specified.
     *
     * Create a bounded cache with a protector function. Insert several objects
     * into the cache. Assert that no protected objects are removed.
     */
    @Test
    public void testProtector() {
        Cache<Integer, Integer> cache = new Cache<>(2);
        cache.setProtector(ref -> {
            Integer integer = ref.get();
            if (integer != null && (integer % 2) == 0) {
                return true; // Protect even numbers.
            }
            return false;
        });
        for (int i = 0; i < 5; i += 1) {
            cache.storeValue(i, i);
        }
        // Note that the cache has a max cost of 2 and a total cost of 3.
        assertTrue(cache.hasValue(0));
        assertFalse(cache.hasValue(1));
        assertTrue(cache.hasValue(2));
        assertFalse(cache.hasValue(3));
        assertTrue(cache.hasValue(4));
    }
}
