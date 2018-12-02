// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.params;

public interface ParamLoader
{
    String getParam(final String p0, final String p1, final String p2);
    
    String getParam(final String p0, final String p1);
    
    String getParam(final String p0);
    
    void setParam(final String p0, final String p1);
    
    void removeParam(final String p0);
    
    void reload(final String p0);
}
