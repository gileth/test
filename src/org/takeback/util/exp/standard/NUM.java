// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import java.math.BigDecimal;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class NUM extends Expression
{
    public NUM() {
        this.name = "d";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            Number result = 0;
            final Object lso = ls.get(1);
            if (lso instanceof List) {
                result = ConversionUtils.convert(processor.run((List<?>)lso), Number.class);
            }
            else {
                result = ConversionUtils.convert(ls.get(1), Number.class);
            }
            if (ls.size() == 3) {
                final int scale = ConversionUtils.convert(ls.get(2), Integer.TYPE);
                result = BigDecimal.valueOf(ConversionUtils.convert(result, Double.class)).setScale(scale, 4).doubleValue();
            }
            return result;
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        Number result = 0;
        final Object lso = ls.get(1);
        if (lso instanceof List) {
            result = ConversionUtils.convert(processor.run((List<?>)lso), Number.class);
        }
        else {
            result = ConversionUtils.convert(ls.get(1), Number.class);
        }
        return String.valueOf(result);
    }
}
