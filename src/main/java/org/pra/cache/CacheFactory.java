package org.pra.cache;

import org.pra.cache.impl.LruCache;
import org.pra.cache.impl.TimedCache;

/**
 * @author Pradeep Jindal
 * Created by pjind5 on 05-Jul-17.
 */
public class CacheFactory {

    public static <K,V> Cache<K,V> newLruCache() {
        return new CacheService<>(new LruCache<>());
    }

    public static <K,V> Cache<K,V> newTimedCache() {
        return new CacheService<>(new TimedCache<>());
    }

    public static <K,V> Cache<K,V> newSuppliedCache(Cache<K,V> cache) {
        return new CacheService<>(cache);
    }
}
