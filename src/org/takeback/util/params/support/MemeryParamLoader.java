// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.params.support;

import com.google.common.collect.Maps;
import org.takeback.util.params.Param;
import java.util.Map;
import org.takeback.util.params.ParamLoader;

public class MemeryParamLoader implements ParamLoader
{
    protected Map<String, Param> params;
    
    public MemeryParamLoader() {
        this.params = (Map<String, Param>)Maps.newConcurrentMap();
    }
    
    @Override
    public String getParam(final String parName, final String defaultValue, final String paramalias) {
        Param p = this.params.get(parName);
        if (p != null) {
            return p.getParamvalue();
        }
        if (defaultValue == null) {
            return null;
        }
        p = new Param(parName, defaultValue, paramalias);
        this.params.put(parName, p);
        return defaultValue;
    }
    
    @Override
    public String getParam(final String parName, final String defaultValue) {
        return this.getParam(parName, defaultValue, null);
    }
    
    @Override
    public String getParam(final String parName) {
        return this.getParam(parName, null, null);
    }
    
    @Override
    public void setParam(final String parName, final String value) {
        final Param p = new Param(parName, value);
        this.params.put(parName, p);
    }
    
    @Override
    public void removeParam(final String parName) {
        this.reload(parName);
    }
    
    @Override
    public void reload(final String parName) {
        this.params.remove(parName);
    }
}
