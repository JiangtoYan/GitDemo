package com.sccp.library.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sccp.library.entity.CacheObject;

public interface Cache<K, V> {

    /**
     * get object in cache
     * 
     * @return
     */
    int getSize();

    /**
     * get object
     * 
     * @param key
     * @return
     */
    CacheObject<V> get(K key);

    /**
     * put object
     * 
     * @param key key
     * @param value data in object, {@link CacheObject#getData()}
     * @return
     */
    CacheObject<V> put(K key, V value);

    /**
     * put object
     * 
     * @param key key
     * @param value object
     * @return
     */
    CacheObject<V> put(K key, CacheObject<V> value);

    /**
     * put all object in cache2
     * 
     * @param cache2
     */
    void putAll(Cache<K, V> cache2);

    /**
     * whether key is in cache
     * 
     * @param key
     * @return
     */
    boolean containsKey(K key);

    /**
     * remove object
     * 
     * @param key
     * @return the object be removed
     */
    CacheObject<V> remove(K key);

    /**
     * clear cache
     */
    void clear();

    /**
     * get hit rate
     * 
     * @return
     */
    double getHitRate();

    /**
     * key set
     * 
     * @return
     */
    Set<K> keySet();

    /**
     * key value set
     * 
     * @return
     */
    Set<Map.Entry<K, CacheObject<V>>> entrySet();

    /**
     * value set
     * 
     * @return
     */
    Collection<CacheObject<V>> values();
}
