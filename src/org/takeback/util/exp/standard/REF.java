// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.converter.ConversionUtils;
import java.util.HashMap;
import java.util.Map;
import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.context.ContextUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class REF extends Expression
{
    public REF() {
        this.symbol = "$";
        this.name = this.symbol;
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            String nm = (String)ls.get(1);
            if (nm.startsWith("%")) {
                nm = nm.substring(1);
            }
            return ContextUtils.get(nm);
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            final String nm = (String)ls.get(1);
            if (!nm.startsWith("%")) {
                return nm;
            }
            final boolean forPreparedStatement = ContextUtils.get("$exp.forPreparedStatement", Boolean.TYPE);
            final Object o = this.run(ls, processor);
            if (forPreparedStatement) {
                final Map<String, Object> parameters = ContextUtils.get("$exp.statementParameters", (Class<Map<String, Object>>)HashMap.class);
                final String key = "arg" + parameters.size();
                parameters.put(key, o);
                return ":" + key;
            }
            final String s = ConversionUtils.convert(o, String.class);
            if (o instanceof Number) {
                return s;
            }
            return "'" + s + "'";
        }
        catch (Exception e) {
            throw new ExprException(e);
        }
    }
}
