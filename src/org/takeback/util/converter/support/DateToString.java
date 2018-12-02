// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.joda.time.format.DateTimeFormat;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class DateToString implements Converter<Date, String>
{
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public String convert(final Date source) {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(source.getTime());
    }
}
