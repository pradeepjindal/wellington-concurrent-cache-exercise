package org.pra.cache.impl;

import org.pra.cache.Cache;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Pradeep Jindal
 * Created by pjind5 on 04-Jul-17.
 */
public class TimedCache<K,V> extends ConcurrentHashMap<K,TimedCache.Wrapper<V>> implements Cache<K,V> {

    private volatile boolean cacheEnabled = false;
    private final AtomicInteger cacheHit;
    private final AtomicInteger cacheMiss;
    private final long          evictAfterMilliSeconds;


    public TimedCache() {
        this(60 * 1000);
    }

    public TimedCache(long evictAfterMilliSeconds) {
        super();
        cacheEnabled = true;
        cacheHit = new AtomicInteger();
        cacheMiss = new AtomicInteger();

        Thread cacheEvictingThread;
        this.evictAfterMilliSeconds = evictAfterMilliSeconds;
        cacheEvictingThread = new Thread(new CacheEviction());
        cacheEvictingThread.setDaemon(true);
        cacheEvictingThread.start();
        //System.out.println( " cache state = " + cacheEvictingThread.getState());
        Executors.newFixedThreadPool(1);
    }
    //
    @Override
    public Boolean containsItemKey(K key) {
        return containsKey(key);
    }

    @Override
    public V getItem(K key) {
        if(containsItemKey(key)) {
            cacheHit.incrementAndGet();
            return get(key).getUserObject();
        } else {
            cacheMiss.incrementAndGet();
            return null;
        }
    }

    @Override
    public void putItem(K key, V value) {
        Wrapper<V> wrapper = new Wrapper<>(value);
        put(key, wrapper);
    }

    @Override
    public void putAllItems(Map<K, V> map) {
        map.forEach(this::putItem);
    }

    @Override
    public void removeItem(K key) {
        remove(key);
    }

    @Override
    public Integer getSize() {
        return size();
    }

    @Override
    public void reset() {
        clear();
    }

    @Override
    public void close() {
        cacheEnabled = false;
        clear();
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

    //
    private class CacheEviction implements Runnable {
        @Override
        public void run() {
            while(cacheEnabled) {
                try {
                    //System.out.println( " chm going to sleep ");
                    Thread.sleep(evictAfterMilliSeconds);
                    evict();
                    //System.out.println( " rerunning from evict ");
                } catch (InterruptedException ie) {
                    //log
                }
            }
        }
        private void evict() {
            if (size() == 0) {
                //System.out.println( " size is zero ");
                return;
            }
            long timeElapsed = System.currentTimeMillis();
            //System.out.println( " evicting = ");
            forEach( (key, wrapper) -> {
                if( (timeElapsed - wrapper.getLastTime()) > evictAfterMilliSeconds) {
                    //System.out.println( " removing = " + key);
                    remove(key);
                } else {
                    System.out.println( " not removing = " + key);
                }
            });
        }
    }

    static class Wrapper<V> {
        V userObject;
        long lastTime;

        Wrapper(V value) {
            this.userObject = value;
            this.lastTime = System.currentTimeMillis();
        }

        V getUserObject() {
            return userObject;
        }

        long getLastTime() {
            return lastTime;
        }
        public void setLastTime(long lastTime) {
            this.lastTime = lastTime;
        }
    }
}
