// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp;

import java.util.Date;
import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import java.util.List;

public class ExpressionUtils
{
    public static Number toNumber(final Object lso, final ExpressionProcessor processor) throws ExprException {
        try {
            Number v = null;
            if (lso instanceof List) {
                v = ConversionUtils.convert(processor.run((List<?>)lso), Number.class);
            }
            else {
                v = ConversionUtils.convert(lso, Number.class);
            }
            return v;
        }
        catch (Exception e) {
            throw new ExprException(e);
        }
    }
    
    public static String toString(final Object lso, final ExpressionProcessor processor) throws ExprException {
        try {
            String s = null;
            if (lso instanceof List) {
                s = processor.toString((List<?>)lso);
            }
            else if (lso instanceof String) {
                s = "'" + lso + "'";
            }
            else {
                s = ConversionUtils.convert(lso, String.class);
                if (lso instanceof Date) {
                    return "'" + s + "'";
                }
            }
            return s;
        }
        catch (Exception e) {
            throw new ExprException(e);
        }
    }
}
