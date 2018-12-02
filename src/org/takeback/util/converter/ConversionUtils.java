// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter;

import org.takeback.util.converter.support.ObjectToMap;
import org.takeback.util.converter.support.MapToObject;
import org.takeback.util.converter.support.ObjectToElement;
import org.springframework.core.convert.converter.GenericConverter;
import org.takeback.util.converter.support.ElementToObject;
import org.takeback.util.converter.support.ElementToString;
import org.takeback.util.converter.support.DocumentToString;
import org.takeback.util.converter.support.StringToInetSocketAddress;
import org.takeback.util.converter.support.StringToElement;
import org.takeback.util.converter.support.StringToDocument;
import org.takeback.util.converter.support.StringToList;
import org.takeback.util.converter.support.StringToMap;
import org.takeback.util.converter.support.StringToTimestamp;
import org.takeback.util.converter.support.StringToSQLTime;
import org.takeback.util.converter.support.StringToSQLDate;
import org.takeback.util.converter.support.StringToDate;
import org.takeback.util.converter.support.IntegerToBoolean;
import org.takeback.util.converter.support.DateToString;
import org.takeback.util.converter.support.DateToNumber;
import org.takeback.util.converter.support.DateToLong;
import org.takeback.util.converter.support.LongToDate;
import org.springframework.core.convert.support.DefaultConversionService;
import java.util.Iterator;
import org.springframework.core.convert.converter.Converter;
import java.util.Set;
import org.springframework.core.convert.support.ConfigurableConversionService;

public class ConversionUtils
{
    private static ConfigurableConversionService conversion;
    
    public void setConverters(final Set<Converter> converters) {
        for (final Converter c : converters) {
            ConversionUtils.conversion.addConverter(c);
        }
    }
    
    public static <T> T convert(final Object source, final Class<T> targetType) {
        if (targetType.isInstance(source)) {
            return (T)source;
        }
        return (T)ConversionUtils.conversion.convert(source, (Class)targetType);
    }
    
    public static boolean canConvert(final Class<?> sourceType, final Class<?> targetType) {
        return ConversionUtils.conversion.canConvert((Class)sourceType, (Class)targetType);
    }
    
    static {
        (ConversionUtils.conversion = (ConfigurableConversionService)new DefaultConversionService()).addConverter((Converter)new LongToDate());
        ConversionUtils.conversion.addConverter((Converter)new DateToLong());
        ConversionUtils.conversion.addConverter((Converter)new DateToNumber());
        ConversionUtils.conversion.addConverter((Converter)new DateToString());
        ConversionUtils.conversion.addConverter((Converter)new IntegerToBoolean());
        ConversionUtils.conversion.addConverter((Converter)new StringToDate());
        ConversionUtils.conversion.addConverter((Converter)new StringToSQLDate());
        ConversionUtils.conversion.addConverter((Converter)new StringToSQLTime());
        ConversionUtils.conversion.addConverter((Converter)new StringToTimestamp());
        ConversionUtils.conversion.addConverter((Converter)new StringToMap());
        ConversionUtils.conversion.addConverter((Converter)new StringToList());
        ConversionUtils.conversion.addConverter((Converter)new StringToDocument());
        ConversionUtils.conversion.addConverter((Converter)new StringToElement());
        ConversionUtils.conversion.addConverter((Converter)new StringToInetSocketAddress());
        ConversionUtils.conversion.addConverter((Converter)new DocumentToString());
        ConversionUtils.conversion.addConverter((Converter)new ElementToString());
        ConversionUtils.conversion.addConverter((GenericConverter)new ElementToObject());
        ConversionUtils.conversion.addConverter((GenericConverter)new ObjectToElement());
        ConversionUtils.conversion.addConverter((GenericConverter)new MapToObject());
        ConversionUtils.conversion.addConverter((GenericConverter)new ObjectToMap());
    }
}
