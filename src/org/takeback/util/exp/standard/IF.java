// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class IF extends Expression
{
    public IF() {
        this.symbol = "if";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final boolean status = (boolean)processor.run((List<?>)ls.get(1));
            Object result = null;
            if (status) {
                result = ls.get(2);
                if (result instanceof List) {
                    result = processor.run((List<?>)result);
                }
            }
            else {
                result = ls.get(3);
                if (result instanceof List) {
                    result = processor.run((List<?>)result);
                }
            }
            return result;
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final StringBuffer sb = new StringBuffer(processor.toString((List<?>)ls.get(1)));
            sb.append(" ? ");
            Object lso = ls.get(2);
            if (lso instanceof List) {
                sb.append(processor.toString((List<?>)lso));
            }
            else {
                sb.append(ConversionUtils.convert(lso, String.class));
            }
            sb.append(" : ");
            lso = ls.get(3);
            if (lso instanceof List) {
                sb.append(processor.toString((List<?>)ls.get(3)));
            }
            else {
                sb.append(ConversionUtils.convert(lso, String.class));
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
}
