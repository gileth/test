// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class SUM extends Expression
{
    public SUM() {
        this.symbol = "+";
        this.needBrackets = true;
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            Number result = null;
            Object lso = ls.get(1);
            if (lso instanceof List) {
                result = (Number)processor.run((List<?>)lso);
            }
            else {
                result = ConversionUtils.convert(lso, Number.class);
            }
            for (int i = 2, size = ls.size(); i < size; ++i) {
                Number v = null;
                lso = ls.get(i);
                if (lso instanceof List) {
                    v = (Number)processor.run((List<?>)lso);
                }
                else {
                    v = ConversionUtils.convert(lso, Number.class);
                }
                result = result.doubleValue() + v.doubleValue();
            }
            return result;
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
}
