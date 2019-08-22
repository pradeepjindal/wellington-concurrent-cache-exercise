package org.pra.cache.impl;

import org.pra.cache.Cache;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Pradeep Jindal
 * Created by pjind5 on 04-Jul-17.
 */
public class LruCache<K,V> extends LinkedHashMap<K,V> implements Cache<K,V> {

    private volatile boolean cacheEnabled = false;
    private final ReentrantLock modificationLock;
    private final AtomicInteger cacheHit;
    private final AtomicInteger cacheMiss;
    private final int           maxSize;

    public LruCache() {
        this(Integer.MAX_VALUE);
    }

    public LruCache(int maxSize) {
        super();
        modificationLock = new ReentrantLock();
        cacheEnabled = true;
        cacheHit = new AtomicInteger();
        cacheMiss = new AtomicInteger();
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry eldest) {
        return size() > maxSize;
    }

    //
    @Override
    public Boolean containsItemKey(K key) {
        return containsKey(key);
    }

    @Override
    public V getItem(K key) {
        if(containsKey(key)) {
            cacheHit.incrementAndGet();
        } else {
            cacheMiss.incrementAndGet();
        }
        return get(key);
    }

    @Override
    public void putItem(K key, V value) {
        modificationLock.lock();
        put(key, value);
        modificationLock.unlock();
    }

    @Override
    public void putAllItems(Map<K, V> map) {
        modificationLock.lock();
        putAll(map);
        modificationLock.unlock();
    }

    @Override
    public void removeItem(K key) {
        modificationLock.lock();
        remove(key);
        modificationLock.unlock();
    }

    @Override
    public Integer getSize() {
        return size();
    }

    @Override
    public void reset() {
        modificationLock.lock();
        clear();
        modificationLock.unlock();
    }

    @Override
    public void close() {
        cacheEnabled = false;
        reset();
    }

    @Override
    public Boolean isClosed() {
        return cacheEnabled;
    }

    @Override
    public Integer getHitCount() {
        return cacheHit.get();
    }

    @Override
    public Integer getMissCount() {
        return cacheMiss.get();
    }

    @Override
    public Float getCacheHitMissRatio() {
        float hit = cacheHit.get();
        float total = cacheHit.get() + cacheMiss.get();
        float ratio = hit/total;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Float.valueOf(decimalFormat.format(ratio));
    }
}
