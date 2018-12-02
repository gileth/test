// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class SUBSTR extends Expression
{
    public SUBSTR() {
        this.symbol = "substring";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final String str = (String)processor.run((List<?>)ls.get(1));
            final int start = ConversionUtils.convert(ls.get(2), Integer.class);
            if (ls.size() == 4) {
                final int end = ConversionUtils.convert(ls.get(3), Integer.class);
                return str.substring(start, end);
            }
            return str.substring(start);
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final String str = processor.toString((List<?>)ls.get(1));
            final int start = ConversionUtils.convert(ls.get(2), Integer.class);
            final StringBuffer sb = new StringBuffer(this.symbol).append("(").append(str).append(",").append(start);
            if (ls.size() == 4) {
                final int end = ConversionUtils.convert(ls.get(3), Integer.class);
                sb.append(",").append(end);
            }
            sb.append(")");
            return sb.toString();
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
}
