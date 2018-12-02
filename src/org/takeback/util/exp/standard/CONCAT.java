// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class CONCAT extends Expression
{
    public CONCAT() {
        this.symbol = "concat";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final StringBuffer sb = new StringBuffer();
            for (int i = 1, size = ls.size(); i < size; ++i) {
                Object o = ls.get(i);
                if (o instanceof List) {
                    o = processor.run((List<?>)o);
                }
                sb.append(ConversionUtils.convert(o, String.class));
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        return "(" + super.toString(ls, processor) + ")";
    }
}
