// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import org.dozer.DozerBeanMapperSingletonWrapper;
import java.util.Map;
import java.util.HashMap;
import org.takeback.util.converter.ConversionUtils;
import org.mvel2.MVEL;
import org.dozer.Mapper;

public class BeanUtils
{
    private static final Mapper dozer;
    
    public static <T> T map(final Object source, final Class<T> destinationClass) {
        return (T)BeanUtils.dozer.map(source, (Class)destinationClass);
    }
    
    public static <T> T map(final Object source, final Object dest) {
        BeanUtils.dozer.map(source, dest);
        return (T)dest;
    }
    
    public static void copy(final Object source, final Object dest) {
        BeanUtils.dozer.map(source, dest);
    }
    
    public static Object getProperty(final Object bean, final String nm) {
        Object val = null;
        val = MVEL.getProperty(nm, bean);
        return val;
    }
    
    public static <T> T getProperty(final Object bean, final String nm, final Class<T> type) {
        final Object val = getProperty(bean, nm);
        return ConversionUtils.convert(val, type);
    }
    
    public static void setProperty(final Object bean, final String nm, final Object v) {
        MVEL.setProperty(bean, nm, v);
    }
    
    public static void setPropertyInMap(final Object bean, final String nm, final Object v) throws Exception {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("key", nm);
        vars.put("value", v);
        MVEL.eval("setProperty(key,value)", bean, (Map)vars);
    }
    
    static {
        dozer = DozerBeanMapperSingletonWrapper.getInstance();
    }
}
