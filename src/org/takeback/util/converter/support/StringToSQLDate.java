// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.joda.time.format.DateTimeFormat;
import java.sql.Date;
import org.springframework.core.convert.converter.Converter;

public class StringToSQLDate implements Converter<String, Date>
{
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    
    public Date convert(final String source) {
        return new Date(DateTimeFormat.forPattern("yyyy-MM-dd").parseMillis(source));
    }
}
