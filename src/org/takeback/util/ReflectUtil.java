// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.lang.reflect.GenericDeclaration;
import java.sql.Time;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import org.springframework.core.DefaultParameterNameDiscoverer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.google.common.collect.ImmutableSet;
import org.springframework.core.ParameterNameDiscoverer;

public class ReflectUtil
{
    private static final ParameterNameDiscoverer localVarDiscoverer;
    private static final ImmutableSet<Class<?>> PrimitiveBigTypes;
    
    public static boolean isSimpleType(final Class<?> c) {
        return c.isPrimitive() || ReflectUtil.PrimitiveBigTypes.contains((Object)c);
    }
    
    public static boolean isCompatible(Class<?> c, final Object o) {
        final boolean pt = c.isPrimitive();
        if (o == null) {
            return !pt;
        }
        if (pt) {
            if (c == Integer.TYPE) {
                c = Integer.class;
            }
            else if (c == Boolean.TYPE) {
                c = Boolean.class;
            }
            else if (c == Long.TYPE) {
                c = Long.class;
            }
            else if (c == Float.TYPE) {
                c = Float.class;
            }
            else if (c == Double.TYPE) {
                c = Double.class;
            }
            else if (c == Character.TYPE) {
                c = Character.class;
            }
            else if (c == Byte.TYPE) {
                c = Byte.class;
            }
            else if (c == Short.TYPE) {
                c = Short.class;
            }
        }
        return c == o.getClass() || c.isInstance(o);
    }
    
    public static String getPrimitiveBigTypeName(final String p) {
        Class<?> c = null;
        switch (p) {
            case "int": {
                c = Integer.class;
                break;
            }
            case "boolean": {
                c = Boolean.class;
                break;
            }
            case "long": {
                c = Long.class;
                break;
            }
            case "float": {
                c = Float.class;
                break;
            }
            case "double": {
                c = Double.class;
                break;
            }
            case "char": {
                c = Character.class;
                break;
            }
            case "byte": {
                c = Byte.class;
            }
            case "short": {
                c = Short.class;
                break;
            }
        }
        if (c == null) {
            return null;
        }
        return c.getName();
    }
    
    public static Type findParentParameterizedType(final Class<?> clz) {
        return findParentParameterizedType(clz, 0);
    }
    
    public static Type findParentParameterizedType(final Class<?> clz, final int index) {
        Class<?> suprClz;
        for (Type supr = clz.getGenericSuperclass(); supr != null; supr = suprClz.getGenericSuperclass()) {
            if (supr instanceof ParameterizedType) {
                final ParameterizedType tp = (ParameterizedType)supr;
                final Type resultClz = tp.getActualTypeArguments()[index];
                return resultClz;
            }
            suprClz = (Class<?>)supr;
        }
        return null;
    }
    
    public static Type findTypeVariableParameterizedType(final TypeVariable<?> tv, final Class<?> clz) {
        final String nm = tv.getName();
        TypeVariable<?>[] clsTvs;
        int i;
        TypeVariable<?> clsTv;
        for (clsTvs = ((GenericDeclaration)tv.getGenericDeclaration()).getTypeParameters(), i = 0; i < clsTvs.length; ++i) {
            clsTv = clsTvs[i];
            if (nm.equals(clsTv.getName())) {
                break;
            }
        }
        return findParentParameterizedType(clz, i);
    }
    
    public static String getCodeBase(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final ProtectionDomain domain = cls.getProtectionDomain();
        if (domain == null) {
            return null;
        }
        final CodeSource source = domain.getCodeSource();
        if (source == null) {
            return null;
        }
        final URL location = source.getLocation();
        if (location == null) {
            return null;
        }
        return location.getFile();
    }
    
    public static String getName(Class<?> c) {
        if (c.isArray()) {
            final StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            } while (c.isArray());
            return c.getName() + sb.toString();
        }
        return c.getName();
    }
    
    public static String getName(final Method m) {
        final StringBuilder ret = new StringBuilder();
        ret.append(getName(m.getReturnType())).append(' ');
        ret.append(m.getName()).append('(');
        final Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (i > 0) {
                ret.append(',');
            }
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }
    
    public static String getName(final Constructor<?> c) {
        final StringBuilder ret = new StringBuilder("(");
        final Class<?>[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (i > 0) {
                ret.append(',');
            }
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }
    
    public static String[] getMethodParameterNames(final Method m) {
        return ReflectUtil.localVarDiscoverer.getParameterNames(m);
    }
    
    static {
        localVarDiscoverer = (ParameterNameDiscoverer)new DefaultParameterNameDiscoverer();
        PrimitiveBigTypes = ImmutableSet.builder().add((Object[])new Class[] { Integer.class, Character.class, Boolean.class, Long.class, Float.class, Double.class, Character.class, Byte.class, Short.class, String.class, Date.class, Timestamp.class, BigDecimal.class, java.sql.Date.class, Time.class }).build();
    }
}
