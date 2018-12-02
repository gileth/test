// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.takeback.util.JSONUtils;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

public class StringToMap implements Converter<String, Map>
{
    public Map convert(final String source) {
        return JSONUtils.parse(source, Map.class);
    }
}
