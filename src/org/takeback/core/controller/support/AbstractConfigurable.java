// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.controller.support;

import org.takeback.util.converter.ConversionUtils;
import java.util.HashMap;
import java.util.Map;
import org.takeback.core.controller.Configurable;

public abstract class AbstractConfigurable implements Configurable
{
    private static final long serialVersionUID = 4078730957151852441L;
    protected Long lastModi;
    protected String id;
    protected Map<String, Object> properties;
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public void setId(final String id) {
        this.id = id;
    }
    
    @Override
    public void setProperty(final String nm, final Object v) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.put(nm, v);
    }
    
    @Override
    public Object getProperty(final String nm) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(nm);
    }
    
    @Override
    public <T> T getProperty(final String nm, final Class<T> targetType) {
        return ConversionUtils.convert(this.getProperty(nm), targetType);
    }
    
    @Override
    public Map<String, Object> getProperties() {
        if (this.properties == null || this.properties.size() == 0) {
            return null;
        }
        return this.properties;
    }
    
    @Override
    public Long getlastModify() {
        return this.lastModi;
    }
    
    @Override
    public void setLastModify(final Long lastModi) {
        this.lastModi = lastModi;
    }
}
