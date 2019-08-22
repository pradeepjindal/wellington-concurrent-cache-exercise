package org.pra.cache;

import java.util.Map;

/**
 * Created by pjind5 on 04-Jul-17.
 */
public interface Cache<K,V> {

    Boolean containsItemKey(K key);
    V getItem(K key);
    void putItem(K key, V value);
    void putAllItems(Map<K,V> map);
    void removeItem(K key);
    Integer getSize();

    void reset();
    void close();
    Boolean isClosed();

    Integer getHitCount();
    Integer getMissCount();
    Float getCasheHitMissRatio();
}
