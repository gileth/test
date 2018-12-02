// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.exp.ExpressionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class BETWEEN extends Expression
{
    public BETWEEN() {
        this.symbol = "between";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final double v = ExpressionUtils.toNumber(ls.get(1), processor).doubleValue();
            final double low = ExpressionUtils.toNumber(ls.get(2), processor).doubleValue();
            final double high = ExpressionUtils.toNumber(ls.get(3), processor).doubleValue();
            return low < v && v < high;
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final StringBuffer sb = new StringBuffer(ExpressionUtils.toString(ls.get(1), processor));
            sb.append(" between ").append(ExpressionUtils.toString(ls.get(2), processor)).append(" and ").append(ExpressionUtils.toString(ls.get(3), processor));
            return sb.toString();
        }
        catch (Exception e) {
            throw new ExprException(e);
        }
    }
}
