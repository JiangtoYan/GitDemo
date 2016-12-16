package com.sccp.library.entity;

import java.io.Serializable;

public class CacheObject<V> implements Serializable, Comparable<CacheObject<V>> {

    private static final long serialVersionUID = 1L;

    /** time first put into cache, in mills **/
    protected long            enterTime;
    /** time last used(got), in mills **/
    protected long            lastUsedTime;
    /** used(got) count **/
    protected long            usedCount;
    /** priority, default is zero **/
    protected int             priority;

    /** whether has expired, default is false **/
    protected boolean         isExpired;
    /** whether is valid forever, default is false **/
    protected boolean         isForever;

    /** data **/
    protected V               data;

    public CacheObject() {
        this.enterTime = System.currentTimeMillis();
        this.lastUsedTime = System.currentTimeMillis();
        this.usedCount = 0;
        this.priority = 0;
        this.isExpired = false;
        this.isForever = false;
    }

    public CacheObject(V data) {
        this();
        this.data = data;
    }

    /**
     * Get time first put into cache, in mills
     * 
     * @return
     */
    public long getEnterTime() {
        return enterTime;
    }

    /**
     * Set time first put into cache, in mills
     * 
     * @param enterTime
     */
    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    /**
     * Get time last used(got), in mills
     * 
     * @return
     */
    public long getLastUsedTime() {
        return lastUsedTime;
    }

    /**
     * Set time last used(got), in mills
     * 
     * @param lastUsedTime
     */
    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    /**
     * Get used(got) count
     * 
     * @return
     */
    public long getUsedCount() {
        return usedCount;
    }

    /**
     * Set used(got) count
     * 
     * @param usedCount
     */
    public void setUsedCount(long usedCount) {
        this.usedCount = usedCount;
    }

    /**
     * Atomically increments by one the used(got) count
     * 
     * @return the previous used(got) count
     */
    public synchronized long getAndIncrementUsedCount() {
        return usedCount++;
    }

    /**
     * Get priority, default is zero
     * 
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Set priority, default is zero
     * 
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Get whether has expired, default is false
     * 
     * @return
     */
    public boolean isExpired() {
        return isExpired;
    }

    /**
     * Set whether has expired, default is false
     * 
     * @param isExpired
     */
    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    /**
     * Get whether is valid forever, default is false
     * 
     * @return
     */
    public boolean isForever() {
        return isForever;
    }

    /**
     * Set whether is valid forever, default is false
     * 
     * @param isForever
     */
    public void setForever(boolean isForever) {
        this.isForever = isForever;
    }

    /**
     * Get data
     * 
     * @return
     */
    public V getData() {
        return data;
    }

    /**
     * Set data
     * 
     * @param data
     */
    public void setData(V data) {
        this.data = data;
    }

    /**
     * compare with data
     * 
     * @param o
     * @return
     */
    @Override
    public int compareTo(CacheObject<V> o) {
        return o == null ? 1 : CacheObject.compare(this.data, o.data);
    }

    /**
     * if data, enterTime, priority, isExpired, isForever all equals
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        CacheObject<V> obj = (CacheObject<V>)(o);
        return (CacheObject.isEquals(this.data, obj.data) && this.enterTime == obj.enterTime
                && this.priority == obj.priority && this.isExpired == obj.isExpired && this.isForever == obj.isForever);
    }

    @Override
    public int hashCode() {
        return data == null ? 0 : data.hashCode();
    }
    
    /**
     * compare two object
     * 
     * @param actual
     * @param expected
     * @return <ul>
     *         <li>if both are null, return true</li>
     *         <li>return actual.{@link Object#equals(Object)}</li>
     *         </ul>
     */
    public static boolean isEquals(Object actual, Object expected) {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }
    
    /**
     * compare two object
     * <ul>
     * <strong>About result</strong>
     * <li>if v1 > v2, return 1</li>
     * <li>if v1 = v2, return 0</li>
     * <li>if v1 < v2, return -1</li>
     * </ul>
     * <ul>
     * <strong>About rule</strong>
     * <li>if v1 is null, v2 is null, then return 0</li>
     * <li>if v1 is null, v2 is not null, then return -1</li>
     * <li>if v1 is not null, v2 is null, then return 1</li>
     * <li>return v1.{@link Comparable#compareTo(Object)}</li>
     * </ul>
     * 
     * @param v1
     * @param v2
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <V> int compare(V v1, V v2) {
        return v1 == null ? (v2 == null ? 0 : -1) : (v2 == null ? 1 : ((Comparable)v1).compareTo(v2));
    }
}