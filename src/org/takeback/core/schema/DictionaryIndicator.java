// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.schema;

import java.util.HashMap;
import org.takeback.util.JSONUtils;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class DictionaryIndicator implements Serializable
{
    private static final long serialVersionUID = 2542057660051407081L;
    private String id;
    private String render;
    private String parentKey;
    private Integer slice;
    private List<?> filter;
    private boolean internal;
    private Map<String, String> properties;
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getRender() {
        return this.render;
    }
    
    public void setRender(final String render) {
        this.render = render;
    }
    
    public String getParentKey() {
        return this.parentKey;
    }
    
    public void setParentKey(final String parentKey) {
        this.parentKey = parentKey;
    }
    
    public Integer getSlice() {
        return this.slice;
    }
    
    public void setSlice(final Integer slice) {
        this.slice = slice;
    }
    
    public List<?> getFilter() {
        return this.filter;
    }
    
    public void setFilter(final String sFilter) {
        this.filter = JSONUtils.parse(sFilter, (Class<List<?>>)List.class);
    }
    
    public boolean isInternal() {
        return this.internal;
    }
    
    public void setInternal(final boolean internal) {
        this.internal = internal;
    }
    
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    public void setProperty(final String nm, final String v) {
        if (this.properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties.put(nm, v);
    }
}
