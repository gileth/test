// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.PyConverter;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class PY extends Expression
{
    public PY() {
        this.symbol = "pingyin";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final Object lso = ls.get(1);
            String str = null;
            if (lso instanceof List) {
                str = (String)processor.run((List<?>)lso);
            }
            else {
                str = ConversionUtils.convert(lso, String.class);
            }
            return PyConverter.getFirstLetter(str);
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        return this.symbol + "(" + processor.toString((List<?>)ls.get(1)) + ")";
    }
}
