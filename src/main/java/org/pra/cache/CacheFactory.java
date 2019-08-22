package org.pra.cache;

import org.pra.cache.impl.LruCache;
import org.pra.cache.impl.TimedCache;

/**
 * Created by pjind5 on 05-Jul-17.
 */
public class CacheFactory {

    public static <K,V> Cache<K,V> newLruCache() {
        return new CacheService<K,V>(new LruCache<K, V>());
    }

    public static <K,V> Cache<K,V> newTimedCache() {
        return new CacheService<K,V>(new TimedCache<K, V>());
    }

    public static <K,V> Cache<K,V> newSuppliedCache(Cache<K,V> cache) {
        return new CacheService<K,V>(cache);
    }
}
