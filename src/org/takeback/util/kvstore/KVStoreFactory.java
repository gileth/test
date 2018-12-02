// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.kvstore;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class KVStoreFactory
{
    private static KVStoreFactory instance;
    private Map<String, KVStore> stores;
    
    private KVStoreFactory() {
        this.stores = new ConcurrentHashMap<String, KVStore>();
        KVStoreFactory.instance = this;
    }
    
    public static KVStoreFactory instance() {
        if (KVStoreFactory.instance == null) {
            KVStoreFactory.instance = new KVStoreFactory();
        }
        return KVStoreFactory.instance;
    }
    
    public KVStore get(final String name) {
        final KVStore st = this.stores.get(name);
        if (st == null) {
            throw new IllegalStateException("kvtore " + name + " not exists");
        }
        return st;
    }
    
    public void setStores(final Map<String, KVStore> stores) {
        this.stores = stores;
    }
}
