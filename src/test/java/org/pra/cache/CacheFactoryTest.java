package org.pra.cache;

import org.pra.cache.impl.IllegalStateCacheException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by pjind5 on 05-Jul-17.
 */
public class CacheFactoryTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testLruFactory() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());
        String key2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = CacheFactory.newLruCache();
        assertNull("key should not exist in cache", cache.getItem(key));
        cache.putItem(key, value);
        assertEquals("retrieving value from cache", value, cache.getItem(key));
        assertNull("should not exist", cache.getItem(key2));
        //
        cache.close();
        assertTrue(cache.isClosed());
        assertTrue(cache.getHitCount() == 1);
        assertTrue(cache.getMissCount() == 2);
        assertTrue(cache.getCasheHitMissRatio().equals(new Float(0.33)));
        //
        exception.expect(IllegalStateCacheException.class);
        cache.putItem(key, value);
    }

    @Test
    public void testTimedFactory() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());
        String key2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = CacheFactory.newTimedCache();
        assertNull("key should not exist in cache", cache.getItem(key));
        cache.putItem(key, value);
        assertEquals("retrieving value from cache", value, cache.getItem(key));
        assertNull("should not exist", cache.getItem(key2));
        //
        cache.close();
        assertTrue(cache.isClosed());
        assertTrue(cache.getHitCount() == 1);
        assertTrue(cache.getMissCount() == 2);
        assertTrue(cache.getCasheHitMissRatio().equals(new Float(0.33)));
        //
        exception.expect(IllegalStateCacheException.class);
        cache.putItem(key, value);
    }
}
