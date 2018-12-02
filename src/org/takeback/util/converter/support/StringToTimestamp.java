// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.takeback.util.converter.ConversionUtils;
import java.util.Date;
import java.sql.Timestamp;
import org.springframework.core.convert.converter.Converter;

public class StringToTimestamp implements Converter<String, Timestamp>
{
    public Timestamp convert(final String source) {
        final Date dt = ConversionUtils.convert(source, Date.class);
        return new Timestamp(dt.getTime());
    }
}
