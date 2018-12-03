// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.io.Writer;
import java.io.OutputStream;
import java.io.Reader;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils
{
    private static ObjectMapper mapper;
    
    public static ObjectMapper getMapper() {
        return JSONUtils.mapper;
    }
    
    public static <T> T parse(final String value, final Class<T> clz) {
        if (StringUtils.isEmpty((CharSequence)value)) {
            return null;
        }
        try {
            return (T)JSONUtils.mapper.readValue(value, (Class)clz);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static <T> T parse(final byte[] bytes, final Class<T> clz) {
        try {
            return (T)JSONUtils.mapper.readValue(bytes, (Class)clz);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static <T> T parse(final InputStream ins, final Class<T> clz) {
        try {
            return (T)JSONUtils.mapper.readValue(ins, (Class)clz);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static <T> T parse(final Reader reader, final Class<T> clz) {
        try {
            return (T)JSONUtils.mapper.readValue(reader, (Class)clz);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static <T> T update(final String value, final T object) {
        try {
            return (T)JSONUtils.mapper.readerForUpdating(object).readValue(value);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String writeValueAsString(final Object o) {
        try {
            return JSONUtils.mapper.writeValueAsString(o);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static void write(final OutputStream outs, final Object o) {
        try {
            JSONUtils.mapper.writeValue(outs, o);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static void write(final Writer writer, final Object o) {
        try {
            JSONUtils.mapper.writeValue(writer, o);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String toString(final Object o) {
        try {
            return JSONUtils.mapper.writeValueAsString(o);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String toString(final Object o, final Class<?> clz) {
        try {
            return JSONUtils.mapper.writerWithType((Class)clz).writeValueAsString(o);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static byte[] toBytes(final Object o) {
        try {
            return JSONUtils.mapper.writeValueAsBytes(o);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    static {
        JSONUtils.mapper = new MyObjectMapper();
    }
}
