package com.sccp.library.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sccp.library.entity.CacheObject;


public class FileCache<K, V> implements Cache<K, V>, Serializable {

    private static final long serialVersionUID = 1L;
    
    private String cacheName;
    
    /** default maximum capacity of the cache **/
    public static final int          DEFAULT_MAX_SIZE = 64;

    /** maximum size of the cache, if not set, use {@link #DEFAULT_MAX_SIZE} **/
    private final int                maxSize;

    /** valid time of elements in cache, in mills. It means not invalid if less than 0 **/
    private long                     validTime;

    /** map to storage element **/
    protected Map<K, CacheObject<V>> cache;
    
	public FileCache(int maxSize) {
		
		if (maxSize <= 0) {
            throw new IllegalArgumentException("The maxSize of cache must be greater than 0.");
        }

        this.maxSize = maxSize;
        this.validTime = -1;
        this.cache = new ConcurrentHashMap<K, CacheObject<V>>(maxSize);
	}

	@Override
	public int getSize() {
		
		return 0;
	}

	@Override
    public CacheObject<V> get(K key) {
		
        CacheObject<V> obj = cache.get(key);
        
        if (obj != null) {
            return obj;
        } else {
            return null;
        }
    }

	@Override
	public CacheObject<V> put(K key, V value) {

        CacheObject<V> obj = new CacheObject<V>();
        obj.setData(value);
        obj.setForever(validTime == -1);
        return put(key, obj);
	}
	
	public synchronized CacheObject<V> put(K key, CacheObject<V> value) {
		
//		if (cache.size() >= maxSize) {
//            if (removeExpired() <= 0) {
//                if (cacheFullRemoveType instanceof RemoveTypeNotRemove) {
//                    return null;
//                }
//                if (fullRemoveOne() == null) {
//                    return null;
//                }
//            }
//        }
//        value.setEnterTime(System.currentTimeMillis());
        cache.put(key, value);
        return value;
	}

	@Override
	public void putAll(Cache<K, V> cache2) {
		
		
	}

	@Override
	public boolean containsKey(K key) {
		
		return false;
	}

	@Override
	public CacheObject<V> remove(K key) {
		cache.remove(key);
		return null;
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public double getHitRate() {
		
		return 0;
	}

	@Override
	public Set<K> keySet() {
		
		return null;
	}

	@Override
	public Set<Entry<K, CacheObject<V>>> entrySet() {
		
		return null;
	}

	@Override
	public Collection<CacheObject<V>> values() {
		
		return null;
	}
}
