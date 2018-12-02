// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.sql.Time;
import org.springframework.core.convert.converter.Converter;

public class StringToSQLTime implements Converter<String, Time>
{
    public Time convert(final String source) {
        return Time.valueOf(source);
    }
}
