// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class DateToLong implements Converter<Date, Long>
{
    public Long convert(final Date source) {
        return source.getTime();
    }
}
