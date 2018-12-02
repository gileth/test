// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.app;

import org.takeback.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import org.takeback.util.context.ContextUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.Serializable;

public abstract class ApplicationNode implements Serializable
{
    private static final long serialVersionUID = 5829201367508285016L;
    public static final String MAIN_TYPE = "1";
    public static final String SIGN = "/";
    protected ApplicationNode parent;
    protected Map<String, ApplicationNode> items;
    protected String id;
    protected String name;
    protected String iconCls;
    protected int deep;
    private String type;
    private String ref;
    private Map<String, Object> properties;
    
    public ApplicationNode() {
        this.items = new LinkedHashMap<String, ApplicationNode>();
    }
    
    public <T extends ApplicationNode> List<T> getItems() {
        final List<T> ls = new ArrayList<T>();
        final Collection<ApplicationNode> c = this.items.values();
        for (final ApplicationNode item : c) {
            if ("1".equals(item.getType())) {
                continue;
            }
            ls.add((T)item);
        }
        return ls;
    }
    
    public void appendChild(final ApplicationNode item) {
        item.setParent(this);
        this.items.put(item.getId(), item);
    }
    
    public ApplicationNode getChild(final String id) {
        return this.items.get(id);
    }
    
    @JsonIgnore
    public Map<String, ApplicationNode> items() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ApplicationNode>)this.items);
    }
    
    public void clearItems() {
        this.items.clear();
    }
    
    protected int getRequestDeep() {
        if (ContextUtils.hasKey("$requestAppNodeDeep")) {
            return (int)ContextUtils.get("$requestAppNodeDeep");
        }
        return Integer.MAX_VALUE;
    }
    
    protected String[] getNodePath() {
        final int size = this.deep + 1;
        final String[] paths = new String[size];
        for (ApplicationNode item = this; item != null; item = item.getParent()) {
            paths[item.deep()] = item.getId();
        }
        return paths;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getIconCls() {
        return this.iconCls;
    }
    
    public void setIconCls(final String icon) {
        this.iconCls = icon;
    }
    
    @JsonIgnore
    protected ApplicationNode getParent() {
        return this.parent;
    }
    
    public int deep() {
        return this.deep;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getRef() {
        return this.ref;
    }
    
    public void setRef(final String ref) {
        this.ref = ref;
    }
    
    public void setParent(final ApplicationNode parent) {
        if (this.parent != null) {
            return;
        }
        this.parent = parent;
        this.deep = parent.deep() + 1;
    }
    
    @Override
    public int hashCode() {
        return this.deep * 31 + this.id.hashCode();
    }
    
    public void setProperty(final String nm, final Object v) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.put(nm, v);
    }
    
    public Object getProperty(final String nm) {
        if (this.properties == null) {
            return null;
        }
        final String s = this.properties.get(nm);
        if (StringUtils.isEmpty((CharSequence)s)) {
            return null;
        }
        Object v = null;
        switch (s.charAt(0)) {
            case '%': {
                v = ContextUtils.get(s.substring(1));
                break;
            }
            case '[': {
                v = JSONUtils.parse(s, List.class);
                break;
            }
            case '{': {
                v = JSONUtils.parse(s, HashMap.class);
                break;
            }
            default: {
                v = s;
                break;
            }
        }
        return v;
    }
    
    public void setProperties(final Map<String, Object> ps) {
        this.properties = ps;
    }
    
    public Map<String, Object> getProperties() {
        if (this.properties == null || this.properties.size() == 0) {
            return null;
        }
        return this.properties;
    }
    
    public String getFullId() {
        if (this.parent != null) {
            return this.parent.getFullId() + "/" + this.id;
        }
        return this.id;
    }
    
    public boolean hasRef() {
        return !StringUtils.isEmpty((CharSequence)this.ref);
    }
}
