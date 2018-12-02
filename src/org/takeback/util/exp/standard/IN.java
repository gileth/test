// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.exception.ExprException;
import java.util.Collection;
import java.util.HashSet;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class IN extends Expression
{
    public IN() {
        this.symbol = "in";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final Object o = processor.run((List<?>)ls.get(1));
            List<?> rang = (List<?>)ls.get(2);
            if (rang.get(0).equals("$")) {
                rang = (List<?>)processor.run(rang);
            }
            final HashSet<Object> set = new HashSet<Object>();
            set.addAll(rang);
            return set.contains(o);
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final Object o = ls.get(1);
            final StringBuffer sb = new StringBuffer();
            if (o instanceof List) {
                final List<?> ls2 = (List<?>)o;
                sb.append(processor.toString(ls2));
            }
            else {
                sb.append((String)o);
            }
            sb.append(" ").append(this.symbol).append("(");
            final List<?> rang = (List<?>)ls.get(2);
            if (rang.get(0).equals("$")) {
                final String s = processor.toString(rang);
                sb.append(s);
            }
            else {
                for (int i = 0, size = rang.size(); i < size; ++i) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    final Object r = rang.get(i);
                    final String s2 = ConversionUtils.convert(r, String.class);
                    if (r instanceof Number) {
                        sb.append(s2);
                    }
                    else {
                        sb.append("'").append(s2).append("'");
                    }
                }
            }
            return sb.append(")").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ExprException(e.getMessage());
        }
    }
}
