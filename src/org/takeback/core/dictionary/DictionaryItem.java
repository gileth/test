// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary;

import java.util.HashMap;
import java.io.Serializable;

public class DictionaryItem implements Serializable
{
    private static final long serialVersionUID = -2624948204291546508L;
    private String key;
    private String text;
    private String mCode;
    private boolean leaf;
    private HashMap<String, Object> properties;
    
    public DictionaryItem() {
    }
    
    public DictionaryItem(final String key, final String text) {
        this.setKey(key);
        this.setText(text);
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
        return this.properties.get(nm);
    }
    
    public HashMap<String, Object> getProperties() {
        if (this.properties != null && this.properties.isEmpty()) {
            return null;
        }
        return this.properties;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public String getMCode() {
        return this.mCode;
    }
    
    public void setMCode(final String mCode) {
        this.mCode = mCode;
    }
    
    public boolean isLeaf() {
        return this.leaf;
    }
    
    public void setLeaf(final boolean leaf) {
        this.leaf = leaf;
    }
}
