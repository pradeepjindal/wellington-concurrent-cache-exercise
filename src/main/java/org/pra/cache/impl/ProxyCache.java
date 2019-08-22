package org.pra.cache.impl;

import org.pra.cache.Cache;

import java.util.Map;

/**
 * @author Pradeep Jindal
 * Created by pjind5 on 01-Jul-17.
 */
public class ProxyCache<K,V> implements Cache<K,V> {

    private final Cache<K,V> cache;
    public ProxyCache(Cache<K,V> cache) {
        this.cache = cache;
    }

    @Override
    public Boolean containsItemKey(K key) {
        throw new IllegalStateCacheException();
    }

    @Override
    public V getItem(K key) {
        throw new IllegalStateCacheException();
    }

    @Override
    public void putItem(K key, V value) {
        throw new IllegalStateCacheException();
    }

    @Override
    public void putAllItems(Map<K, V> map) {
        throw new IllegalStateCacheException();
    }

    @Override
    public void removeItem(K key) {
        throw new IllegalStateCacheException();
    }

    @Override
    public Integer getSize() { throw new IllegalStateCacheException(); }

    @Override
    public void reset() {
        throw new IllegalStateCacheException();
    }

    @Override
    public Boolean isClosed() { return true; }

    @Override
    public void close() { throw new IllegalStateCacheException(); }

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
