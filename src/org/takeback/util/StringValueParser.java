// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.context.ContextUtils;
import org.apache.commons.lang3.StringUtils;

public class StringValueParser
{
    public static boolean isStaticString(final String str) {
        return str.charAt(0) != '%' && str.charAt(0) != '[';
    }
    
    public static <T> T parse(String str, final Class<T> type) {
        if (StringUtils.isEmpty((CharSequence)str)) {
            return null;
        }
        switch (str.charAt(0)) {
            case '%': {
                str = str.trim();
                return ConversionUtils.convert(ContextUtils.get(str.substring(1)), type);
            }
            case '[': {
                str = str.trim();
                try {
                    final List<Object> exp = JSONUtils.parse(str, List.class);
                    return ConversionUtils.convert(ExpressionProcessor.instance().run(exp), type);
                }
                catch (Exception e) {
                    throw new IllegalStateException("error config args:" + str);
                }
            }
        }
        return ConversionUtils.convert(str, type);
    }
}
