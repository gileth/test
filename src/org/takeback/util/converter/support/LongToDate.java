// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class LongToDate implements Converter<Long, Date>
{
    public Date convert(final Long source) {
        return new Date(source);
    }
}
