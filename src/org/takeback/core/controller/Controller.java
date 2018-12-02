// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.controller;

public interface Controller<T extends Configurable>
{
    T get(final String p0);
    
    void add(final T p0);
    
    void reload(final String p0);
    
    boolean isLoaded(final String p0);
    
    void reloadAll();
    
    void setLoader(final ConfigurableLoader<T> p0);
    
    ConfigurableLoader<T> getLoader();
}
