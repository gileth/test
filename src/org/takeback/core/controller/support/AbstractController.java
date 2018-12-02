// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.controller.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import com.google.common.collect.Maps;
import java.util.concurrent.locks.Lock;
import org.takeback.core.controller.ConfigurableLoader;
import java.util.Map;
import org.takeback.core.controller.Controller;
import org.takeback.core.controller.Configurable;

public abstract class AbstractController<T extends Configurable> implements Controller<T>
{
    protected final Map<String, T> store;
    protected ConfigurableLoader<T> loader;
    private final Lock lock;
    
    public AbstractController() {
        this.store = new HashMap<String, T>();
        this.lock = new ReentrantLock();
    }
    
    @Override
    public void setLoader(final ConfigurableLoader<T> loader) {
        this.loader = loader;
    }
    
    @Override
    public ConfigurableLoader<T> getLoader() {
        return this.loader;
    }
    
    @Override
    public boolean isLoaded(final String id) {
        try {
            this.lock.lock();
            return this.store.containsKey(id);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void reload(final String id) {
        try {
            this.lock.lock();
            this.store.remove(id);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void reloadAll() {
        this.store.clear();
    }
    
    @Override
    public T get(final String id) {
        try {
            this.lock.lock();
            if (this.store.containsKey(id)) {
                return this.store.get(id);
            }
        }
        finally {
            this.lock.unlock();
        }
        try {
            this.lock.lock();
            if (this.store.containsKey(id)) {
                return this.store.get(id);
            }
            final T t = this.loader.load(id);
            if (t != null) {
                this.store.put(id, t);
                return t;
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void add(final T t) {
        try {
            this.lock.lock();
            this.store.put(t.getId(), t);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setInitList(final List<T> ls) {
        for (final T t : ls) {
            this.add(t);
        }
    }
}
