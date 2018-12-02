// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.params;

import org.takeback.util.params.support.MemeryParamLoader;

public class ParamUtils
{
    private static ParamLoader paramLoader;
    
    public static void setParamLoader(final ParamLoader paramLoader) {
        ParamUtils.paramLoader = paramLoader;
    }
    
    public static String getParam(final String parName, final String defaultValue, final String paramalias) {
        return ParamUtils.paramLoader.getParam(parName, defaultValue, paramalias);
    }
    
    public static String getParam(final String parName, final String defaultValue) {
        return ParamUtils.paramLoader.getParam(parName, defaultValue);
    }
    
    public static String getParam(final String parName) {
        return ParamUtils.paramLoader.getParam(parName);
    }
    
    public static String getParamSafe(final String parName) {
        return (getParam(parName) == null) ? "" : getParam(parName);
    }
    
    public static void setParam(final String parName, final String value) {
        ParamUtils.paramLoader.setParam(parName, value);
    }
    
    public static void removeParam(final String parName) {
        ParamUtils.paramLoader.removeParam(parName);
    }
    
    public static void reload(final String parName) {
        ParamUtils.paramLoader.reload(parName);
    }
    
    static {
        ParamUtils.paramLoader = new MemeryParamLoader();
    }
}
