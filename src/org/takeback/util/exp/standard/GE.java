// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class GE extends Expression
{
    public GE() {
        this.symbol = ">=";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            Object lso = ls.get(1);
            Number v1 = null;
            if (lso instanceof List) {
                v1 = ConversionUtils.convert(processor.run((List<?>)lso), Number.class);
            }
            else {
                v1 = ConversionUtils.convert(lso, Number.class);
            }
            for (int i = 2, size = ls.size(); i < size; ++i) {
                Number v2 = null;
                lso = ls.get(i);
                if (lso instanceof List) {
                    v2 = ConversionUtils.convert(processor.run((List<?>)lso), Number.class);
                }
                else {
                    v1 = ConversionUtils.convert(lso, Number.class);
                }
                if (v1.doubleValue() < v2.doubleValue()) {
                    return false;
                }
            }
            return true;
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
}
