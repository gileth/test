// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.HashMap;
import org.takeback.core.controller.support.AbstractConfigurable;

public abstract class Dictionary extends AbstractConfigurable
{
    private static final long serialVersionUID = 5186888641454350567L;
    protected HashMap<String, DictionaryItem> items;
    protected String clazz;
    protected String searchField;
    protected String searchFieldEx;
    protected String alias;
    protected boolean isPrivate;
    protected boolean queryOnly;
    protected char searchExSymbol;
    protected char searchKeySymbol;
    
    public Dictionary() {
        this.items = new LinkedHashMap<String, DictionaryItem>();
        this.clazz = "XMLDictionary";
        this.searchField = "mCode";
        this.searchFieldEx = "text";
        this.isPrivate = false;
        this.queryOnly = false;
        this.searchExSymbol = '.';
        this.searchKeySymbol = '/';
    }
    
    public void setClass(final String clazz) {
        this.clazz = clazz;
    }
    
    public String getCls() {
        return this.clazz;
    }
    
    public Dictionary(final String id) {
        this.items = new LinkedHashMap<String, DictionaryItem>();
        this.clazz = "XMLDictionary";
        this.searchField = "mCode";
        this.searchFieldEx = "text";
        this.isPrivate = false;
        this.queryOnly = false;
        this.searchExSymbol = '.';
        this.searchKeySymbol = '/';
        this.id = id;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    public void addItem(final DictionaryItem item) {
        this.items.put(item.getKey(), item);
    }
    
    public void removeItem(final String key) {
        this.items.remove(key);
    }
    
    public DictionaryItem getItem(final String key) {
        return this.items.get(key);
    }
    
    public boolean keyExist(final String key) {
        return this.items.containsKey(key);
    }
    
    public String getText(final String key) {
        if (this.items.containsKey(key)) {
            return this.items.get(key).getText();
        }
        return "";
    }
    
    public List<String> getKey(final String text) {
        final List<String> list = new ArrayList<String>();
        for (final String key : this.items.keySet()) {
            if (text.equals(this.items.get(key).getText())) {
                list.add(key);
            }
        }
        return list;
    }
    
    public List<DictionaryItem> itemsList() {
        final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
        for (final DictionaryItem di : this.items.values()) {
            ls.add(di);
        }
        return ls;
    }
    
    public boolean isPrivate() {
        return this.isPrivate;
    }
    
    public void setIsPrivate(final boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    
    public String getSearchField() {
        return this.searchField;
    }
    
    public void setSearchField(final String searchField) {
        this.searchField = searchField;
    }
    
    public String getSearchFieldEx() {
        return this.searchFieldEx;
    }
    
    public boolean isQueryOnly() {
        return this.queryOnly;
    }
    
    public void setQueryOnly(final boolean queryOnly) {
        this.queryOnly = queryOnly;
    }
    
    public void setSearchFieldEx(final String searchFieldEx) {
        this.searchFieldEx = searchFieldEx;
    }
    
    public char getSearchExSymbol() {
        return this.searchExSymbol;
    }
    
    public void setSearchExSymbol(final char searchExSymbol) {
        this.searchExSymbol = searchExSymbol;
    }
    
    public char getSearchKeySymbol() {
        return this.searchKeySymbol;
    }
    
    public void setSearchKeySymbol(final char searchKeySymbol) {
        this.searchKeySymbol = searchKeySymbol;
    }
    
    public abstract List<DictionaryItem> getSlice(final String p0, final int p1, final String p2);
    
    public void init() {
    }
    
    public HashMap<String, DictionaryItem> getItems() {
        return this.items;
    }
    
    public void setItems(final HashMap<String, DictionaryItem> items) {
        this.items = items;
    }
}
