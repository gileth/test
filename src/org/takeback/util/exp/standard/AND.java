// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class AND extends Expression
{
    public AND() {
        this.symbol = "and";
        this.needBrackets = true;
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            for (int i = 1, size = ls.size(); i < size; ++i) {
                final boolean r = (boolean)processor.run((List<?>)ls.get(i));
                if (!r) {
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
