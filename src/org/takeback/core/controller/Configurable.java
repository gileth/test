// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.controller;

import java.util.Map;
import java.io.Serializable;

public interface Configurable extends Serializable
{
    String getId();
    
    void setId(final String p0);
    
    void setProperty(final String p0, final Object p1);
    
    Object getProperty(final String p0);
    
    Map<String, Object> getProperties();
    
    Long getlastModify();
    
    void setLastModify(final Long p0);
    
     <T> T getProperty(final String p0, final Class<T> p1);
}
