// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class DateToNumber implements Converter<Date, Number>
{
    public Number convert(final Date source) {
        return source.getTime();
    }
}
