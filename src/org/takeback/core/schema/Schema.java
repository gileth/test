// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.schema;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.Map;
import org.takeback.core.controller.support.AbstractConfigurable;

public class Schema extends AbstractConfigurable
{
    private static final long serialVersionUID = -271602734048406147L;
    public static final String KEY_GEN_AUTO = "identity";
    public static final String KEY_GEN_ASSIGN = "assigned";
    private String name;
    private String mapping;
    private String sort;
    private String pkey;
    private Map<String, SchemaItem> items;
    
    public Schema() {
        this.items = (Map<String, SchemaItem>)Maps.newLinkedHashMap();
    }
    
    public Schema(final String id) {
        this.items = (Map<String, SchemaItem>)Maps.newLinkedHashMap();
        this.id = id;
    }
    
    public void addItem(final SchemaItem it) {
        this.items.put(it.getId(), it);
    }
    
    public void removeItem(final String id) {
        this.items.remove(id);
    }
    
    public SchemaItem getItem(final String id) {
        return this.items.get(id);
    }
    
    public List<SchemaItem> getItems() {
        final List<SchemaItem> sis = new ArrayList<SchemaItem>();
        sis.addAll(this.items.values());
        return sis;
    }
    
    public int getSize() {
        return this.items.size();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getMapping() {
        return this.mapping;
    }
    
    public void setMapping(final String mapping) {
        this.mapping = mapping;
    }
    
    public String getSort() {
        return this.sort;
    }
    
    public void setSort(final String sort) {
        this.sort = sort;
    }
    
    public String getPkey() {
        return this.pkey;
    }
    
    public void setPkey(final String pkey) {
        this.pkey = pkey;
    }
}
