package org.pra.cache;

import org.pra.cache.impl.ProxyCache;

import java.util.Map;

/**
 * @author Pradeep Jindal
 * Created by pjind5 on 05-Jul-17.
 */
public class CacheService<K,V> implements Cache<K,V> {

    private Cache<K,V> cache;

    public CacheService(Cache<K,V> cache) {
        this.cache = cache;
    }


    @Override
    public Boolean containsItemKey(K key) {
        return cache.containsItemKey(key);
    }

    @Override
    public V getItem(K key) {
        return cache.getItem(key);
    }

    @Override
    public void putItem(K key, V value) {
        cache.putItem(key, value);
    }

    @Override
    public void putAllItems(Map<K, V> map) {
        cache.putAllItems(map);
    }

    @Override
    public void removeItem(K key) {
        cache.removeItem(key);
    }

    @Override
    public Integer getSize() {
        return cache.getSize();
    }

    @Override
    public void reset() {
        cache.reset();
    }

    @Override
    public void close() {
        cache.close();
        cache = new ProxyCache<>(cache);
    }

    @Override
    public Boolean isClosed() {
        return cache.isClosed();
    }

    @Override
    public Integer getHitCount() {
        return cache.getHitCount();
    }

    @Override
    public Integer getMissCount() {
        return cache.getMissCount();
    }

    @Override
    public Float getCacheHitMissRatio() {
        return cache.getCacheHitMissRatio();
    }
}
