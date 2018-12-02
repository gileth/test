// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import java.util.Date;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class DATE extends Expression
{
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            Date result = null;
            final Object lso = ls.get(1);
            if (lso instanceof List) {
                result = ConversionUtils.convert(processor.run((List<?>)lso), Date.class);
            }
            else {
                result = ConversionUtils.convert(ls.get(1), Date.class);
            }
            return result;
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        return "'" + ConversionUtils.convert(this.run(ls, processor), String.class) + "'";
    }
}
