// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class ISNULL extends Expression
{
    public ISNULL() {
        this.name = "isNull";
    }
    
    @Override
    public Object run(final List ls, final ExpressionProcessor processor) throws ExprException {
        Object lso = ls.get(1);
        if (lso instanceof List) {
            lso = processor.run((List<?>)lso);
        }
        return lso == null;
    }
    
    @Override
    public String toString(final List ls, final ExpressionProcessor processor) throws ExprException {
        Object lso = ls.get(1);
        if (lso instanceof List) {
            lso = processor.toString((List<?>)lso);
        }
        final StringBuffer sb = new StringBuffer(ConversionUtils.convert(lso, String.class));
        sb.append(" is null");
        return sb.toString();
    }
}
