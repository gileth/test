// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.takeback.util.JSONUtils;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

public class StringToList implements Converter<String, List>
{
    public List convert(final String source) {
        return JSONUtils.parse(source, List.class);
    }
}
