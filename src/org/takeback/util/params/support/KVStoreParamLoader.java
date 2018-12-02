// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.params.support;

import org.takeback.util.params.Param;
import org.takeback.util.kvstore.KVStoreFactory;
import org.takeback.util.kvstore.KVStore;

public class KVStoreParamLoader extends MemeryParamLoader
{
    private String storeName;
    private KVStore kvStore;
    
    public KVStoreParamLoader() {
        this.storeName = "paramsStore";
        this.kvStore = KVStoreFactory.instance().get(this.storeName);
    }
    
    public KVStoreParamLoader(final String storeName) {
        this.storeName = "paramsStore";
        this.kvStore = KVStoreFactory.instance().get(storeName);
    }
    
    @Override
    public String getParam(final String parName, final String defaultValue, final String paramalias) {
        Param p = this.params.get(parName);
        if (p != null) {
            return p.getParamvalue();
        }
        final String value = this.kvStore.get(parName);
        if (value != null) {
            this.params.put(parName, new Param(parName, value, paramalias));
            return value;
        }
        if (defaultValue == null) {
            return null;
        }
        p = new Param(parName, defaultValue, paramalias);
        this.params.put(parName, p);
        this.kvStore.put(parName, defaultValue);
        return defaultValue;
    }
    
    @Override
    public void setParam(final String parName, final String value) {
        super.setParam(parName, value);
        this.kvStore.put(parName, value);
    }
    
    @Override
    public void removeParam(final String parName) {
        super.removeParam(parName);
        this.kvStore.remove(parName);
    }
}
