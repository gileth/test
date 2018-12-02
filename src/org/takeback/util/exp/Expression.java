// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp;

import org.takeback.util.exp.exception.ExprException;
import java.util.List;

public abstract class Expression
{
    protected String symbol;
    protected String name;
    protected boolean needBrackets;
    
    public Expression() {
        this.needBrackets = false;
    }
    
    public abstract Object run(final List<?> p0, final ExpressionProcessor p1) throws ExprException;
    
    public String getName() {
        if (this.name != null) {
            return this.name;
        }
        return this.name = this.getClass().getSimpleName().toLowerCase();
    }
    
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final StringBuffer sb = new StringBuffer();
            if (this.needBrackets) {
                sb.append("(");
            }
            for (int i = 1, size = ls.size(); i < size; ++i) {
                if (i > 1) {
                    sb.append(" ").append(this.symbol).append(" ");
                }
                final Object lso = ls.get(i);
                sb.append(ExpressionUtils.toString(lso, processor));
            }
            if (this.needBrackets) {
                sb.append(")");
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new ExprException(e);
        }
    }
}
