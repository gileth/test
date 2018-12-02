// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class LEN extends Expression
{
    public LEN() {
        this.symbol = "len";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        Object o = ls.get(1);
        if (o instanceof List) {
            o = processor.run((List<?>)o);
        }
        final String str = ConversionUtils.convert(o, String.class);
        return str.length();
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        final StringBuffer sb = new StringBuffer(this.symbol).append("(");
        final Object lso = ls.get(1);
        if (lso instanceof List) {
            sb.append(processor.toString((List<?>)lso));
        }
        else {
            sb.append("'").append(ConversionUtils.convert(lso, String.class)).append("'");
        }
        sb.append(")");
        return sb.toString();
    }
}
