// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import org.apache.commons.beanutils.Converter;
import java.util.Date;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.ConvertUtils;
import java.util.Iterator;
import org.apache.commons.beanutils.PropertyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Modifier;
import org.springframework.util.Assert;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

public class ReflectionUtil
{
    private static Log logger;
    
    public static Object invokeGetterMethod(final Object target, final String propertyName) {
        final String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(target, getterMethodName, new Class[0], new Object[0]);
    }
    
    public static void invokeSetterMethod(final Object target, final String propertyName, final Object value) {
        invokeSetterMethod(target, propertyName, value, null);
    }
    
    public static void invokeSetterMethod(final Object target, final String propertyName, final Object value, final Class<?> propertyType) {
        final Class<?> type = (propertyType != null) ? propertyType : value.getClass();
        final String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(target, setterMethodName, new Class[] { type }, new Object[] { value });
    }
    
    public static Object getFieldValue(final Object object, final String fieldName) {
        final Field field = getDeclaredField(object, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }
        makeAccessible(field);
        Object result = null;
        try {
            result = field.get(object);
        }
        catch (IllegalAccessException e) {
            ReflectionUtil.logger.error(("不可能抛出的异常{}" + e.getMessage()));
        }
        return result;
    }
    
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {
        final Field field = getDeclaredField(object, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }
        makeAccessible(field);
        try {
            field.set(object, value);
        }
        catch (IllegalAccessException e) {
            ReflectionUtil.logger.error(("不可能抛出的异常:{}" + e.getMessage()));
        }
    }
    
    public static Object invokeMethod(final Object object, final String methodName, final Class<?>[] parameterTypes, final Object[] parameters) {
        final Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] parameterType " + parameterTypes + " on target [" + object + "]");
        }
        method.setAccessible(true);
        try {
            return method.invoke(object, parameters);
        }
        catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }
    
    protected static Field getDeclaredField(final Object object, final String fieldName) {
        Assert.notNull(object, "object不能为空");
        Assert.hasText(fieldName, "fieldName");
        Class<?> superClass = object.getClass();
        while (superClass != Object.class) {
            try {
                return superClass.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException ex) {
                superClass = superClass.getSuperclass();
                continue;
            }
        }
        return null;
    }
    
    protected static void makeAccessible(final Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }
    
    protected static Method getDeclaredMethod(final Object object, final String methodName, final Class<?>[] parameterTypes) {
        Assert.notNull(object, "object不能为空");
        Class<?> superClass = object.getClass();
        while (superClass != Object.class) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            }
            catch (NoSuchMethodException ex) {
                superClass = superClass.getSuperclass();
                continue;
            }
        }
        return null;
    }
    
    public static <T> Class<T> getSuperClassGenricType(final Class<?> clazz) {
        return (Class<T>)getSuperClassGenricType(clazz, 0);
    }
    
    public static Class getSuperClassGenricType(final Class<?> clazz, final int index) {
        final Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            ReflectionUtil.logger.warn((clazz.getSimpleName() + "'s superclass not ParameterizedType"));
            return Object.class;
        }
        final Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            ReflectionUtil.logger.warn(("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length));
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            ReflectionUtil.logger.warn((clazz.getSimpleName() + " not set the actual class on superclass generic parameter"));
            return Object.class;
        }
        return (Class)params[index];
    }
    
    public static List convertElementPropertyToList(final Collection collection, final String propertyName) {
        final List list = new ArrayList();
        try {
            for (final Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        }
        catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
        return list;
    }
    
    public static String convertElementPropertyToString(final Collection collection, final String propertyName, final String separator) {
        final List list = convertElementPropertyToList(collection, propertyName);
        return StringUtils.join((Iterable)list, separator);
    }
    
    public static <T> T convertStringToObject(final String value, final Class<T> toType) {
        try {
            return (T)ConvertUtils.convert(value, (Class)toType);
        }
        catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }
    
    public static RuntimeException convertReflectionExceptionToUnchecked(final Exception e) {
        return convertReflectionExceptionToUnchecked(null, e);
    }
    
    public static RuntimeException convertReflectionExceptionToUnchecked(String desc, final Exception e) {
        desc = ((desc == null) ? "Unexpected Checked Exception." : desc);
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(desc, e);
        }
        if (e instanceof InvocationTargetException) {
            return new RuntimeException(desc, ((InvocationTargetException)e).getTargetException());
        }
        if (e instanceof RuntimeException) {
            return (RuntimeException)e;
        }
        return new RuntimeException(desc, e);
    }
    
    public static final <T> T getNewInstance(final Class<T> cls) {
        try {
            return cls.newInstance();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return null;
    }
    
    public static void copyPorperties(final Object dest, final Object source, final String[] porperties) throws InvocationTargetException, IllegalAccessException {
        for (final String por : porperties) {
            final Object srcObj = invokeGetterMethod(source, por);
            ReflectionUtil.logger.debug(("属性名：" + por + "------------- 属性值：" + srcObj));
            if (srcObj != null) {
                try {
                    BeanUtils.setProperty(dest, por, srcObj);
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e2) {
                    throw e2;
                }
                catch (InvocationTargetException e3) {
                    throw e3;
                }
            }
        }
    }
    
    public static void copyPorperties(final Object dest, final Object source) throws IllegalAccessException, InvocationTargetException {
        final Class<?> srcCla = source.getClass();
        final Field[] declaredFields;
        final Field[] fsF = declaredFields = srcCla.getDeclaredFields();
        for (final Field s : declaredFields) {
            final String name = s.getName();
            final Object srcObj = invokeGetterMethod(source, name);
            try {
                BeanUtils.setProperty(dest, name, srcObj);
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e2) {
                throw e2;
            }
            catch (InvocationTargetException e3) {
                throw e3;
            }
        }
    }
    
    public static void main(final String[] args) throws InvocationTargetException, IllegalAccessException {
    }
    
    static {
        ReflectionUtil.logger = LogFactory.getLog((Class)ReflectionUtil.class);
        final DateLocaleConverter dc = new DateLocaleConverter();
        ConvertUtils.register((Converter)dc, (Class)Date.class);
    }
}