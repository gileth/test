// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.schema;

import java.util.Date;
import java.math.BigDecimal;
import org.takeback.util.converter.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;

public class DataTypes
{
    public static final String STRING = "string";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String DATE = "date";
    public static final String BIGDECIMAL = "bigDecimal";
    public static final String TIME = "timestamp";
    public static final String DATETIME = "datetime";
    public static final String CHAR = "char";
    public static final String BINARY = "binary";
    public static final String OBJECT = "object";
    private static HashMap<String, Class<?>> types;
    
    public static Class<?> getTypeClass(final String nm) {
        return DataTypes.types.get(StringUtils.uncapitalize(nm));
    }
    
    public static boolean isSupportType(final String type) {
        return DataTypes.types.containsKey(StringUtils.uncapitalize(type));
    }
    
    public static Object toTypeValue(final String type, final Object value) {
        if (!DataTypes.types.containsKey(type)) {
            throw new IllegalStateException("type[" + type + "] is not supported.");
        }
        return ConversionUtils.convert(value, getTypeClass(type));
    }
    
    public static boolean isNumberType(final String type) {
        if (!DataTypes.types.containsKey(type)) {
            return false;
        }
        final Class<?> typeClass = getTypeClass(type);
        return Number.class.isAssignableFrom(typeClass);
    }
    
    static {
        (DataTypes.types = new HashMap<String, Class<?>>()).put("bigDecimal", BigDecimal.class);
        DataTypes.types.put("int", Integer.class);
        DataTypes.types.put("long", Long.class);
        DataTypes.types.put("double", Double.class);
        DataTypes.types.put("string", String.class);
        DataTypes.types.put("date", Date.class);
        DataTypes.types.put("timestamp", Date.class);
        DataTypes.types.put("char", Character.class);
        DataTypes.types.put("boolean", Boolean.class);
        DataTypes.types.put("datetime", Date.class);
        DataTypes.types.put("binary", byte[].class);
        DataTypes.types.put("object", Object.class);
    }
}
