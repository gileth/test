// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.LinkedHashMap;

public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
    private static final long serialVersionUID = -5167631809472116969L;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_MAX_CAPACITY = 1000;
    private volatile int maxCapacity;
    private final Lock lock;
    
    public LRUCache() {
        this(1000);
    }
    
    public LRUCache(final int maxCapacity) {
        super(16, 0.75f, true);
        this.lock = new ReentrantLock();
        this.maxCapacity = maxCapacity;
    }
    
    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return this.size() > this.maxCapacity;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        try {
            this.lock.lock();
            return super.containsKey(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public V get(final Object key) {
        try {
            this.lock.lock();
            return super.get(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public V put(final K key, final V value) {
        try {
            this.lock.lock();
            return super.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public V remove(final Object key) {
        try {
            this.lock.lock();
            return super.remove(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int size() {
        try {
            this.lock.lock();
            return super.size();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void clear() {
        try {
            this.lock.lock();
            super.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int getMaxCapacity() {
        return this.maxCapacity;
    }
    
    public void setMaxCapacity(final int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
