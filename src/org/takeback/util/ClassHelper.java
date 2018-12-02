// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.reflect.Array;
import java.util.Map;

public class ClassHelper
{
    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[L";
    private static final Map<String, Class<?>> primitiveTypeNameMap;
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap;
    
    public static ClassLoader getClassLoader(final Class<?> cls) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable t) {}
        if (cl == null) {
            cl = cls.getClassLoader();
        }
        return cl;
    }
    
    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassHelper.class);
    }
    
    public static Class<?> forName(final String name) throws ClassNotFoundException {
        return forName(name, getClassLoader());
    }
    
    public static Class<?> forName(final String name, final ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        if (name.equals("void")) {
            return Void.TYPE;
        }
        final Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz != null) {
            return clazz;
        }
        if (name.endsWith("[]")) {
            final String elementClassName = name.substring(0, name.length() - "[]".length());
            final Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }
        final int internalArrayMarker = name.indexOf("[L");
        if (internalArrayMarker != -1 && name.endsWith(";")) {
            String elementClassName2 = null;
            if (internalArrayMarker == 0) {
                elementClassName2 = name.substring("[L".length(), name.length() - 1);
            }
            else if (name.startsWith("[")) {
                elementClassName2 = name.substring(1);
            }
            final Class<?> elementClass2 = forName(elementClassName2, classLoader);
            return Array.newInstance(elementClass2, 0).getClass();
        }
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getClassLoader();
        }
        return classLoaderToUse.loadClass(name);
    }
    
    public static Class<?> resolvePrimitiveClassName(final String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 8) {
            result = ClassHelper.primitiveTypeNameMap.get(name);
        }
        return result;
    }
    
    public static Class<?> resolvePrimitiveClassName(final Class<?> type) {
        return ClassHelper.primitiveWrapperTypeMap.get(type);
    }
    
    public static String toShortString(final Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
    }
    
    static {
        primitiveTypeNameMap = new HashMap<String, Class<?>>(16);
        (primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8)).put(Boolean.class, Boolean.TYPE);
        ClassHelper.primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        ClassHelper.primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        ClassHelper.primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        ClassHelper.primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        ClassHelper.primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        ClassHelper.primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        ClassHelper.primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        final Set<Class<?>> primitiveTypeNames = new HashSet<Class<?>>(16);
        primitiveTypeNames.addAll(ClassHelper.primitiveWrapperTypeMap.values());
        primitiveTypeNames.addAll((Collection<? extends Class<?>>)Arrays.asList(boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class));
        for (final Class<?> primitiveClass : primitiveTypeNames) {
            ClassHelper.primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
        }
    }
}
