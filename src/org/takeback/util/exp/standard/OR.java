// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class OR extends Expression
{
    public OR() {
        this.symbol = "or";
        this.needBrackets = true;
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            for (int i = 1, size = ls.size(); i < size; ++i) {
                final boolean r = (boolean)processor.run((List<?>)ls.get(i));
                if (r) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
}
