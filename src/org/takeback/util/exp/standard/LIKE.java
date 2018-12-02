// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.context.Context;
import java.util.HashMap;
import org.takeback.util.context.ContextUtils;
import org.takeback.util.exp.exception.ExprException;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class LIKE extends Expression
{
    public LIKE() {
        this.symbol = "like";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            Object lso = ls.get(1);
            String str1 = null;
            if (lso instanceof List) {
                str1 = ConversionUtils.convert(processor.run((List<?>)lso), String.class);
            }
            else {
                str1 = ConversionUtils.convert(lso, String.class);
            }
            lso = ls.get(2);
            String str2 = null;
            if (lso instanceof List) {
                str2 = ConversionUtils.convert(processor.run((List<?>)lso), String.class);
            }
            else {
                str2 = ConversionUtils.convert(lso, String.class);
            }
            if (!StringUtils.contains((CharSequence)str2, (CharSequence)"%")) {
                str2 += "%";
            }
            final Pattern pattern = Pattern.compile(str2.replaceAll("%", ".*"));
            return pattern.matcher(str1).find();
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        final StringBuffer sb = new StringBuffer();
        Object lso = ls.get(1);
        String str1 = null;
        if (lso instanceof List) {
            str1 = processor.toString((List<?>)lso);
        }
        else {
            str1 = ConversionUtils.convert(lso, String.class);
        }
        sb.append(str1).append(" ").append(this.symbol).append(" ");
        lso = ls.get(2);
        String str2 = null;
        if (lso instanceof List) {
            str2 = processor.toString((List<?>)lso);
            final Context ctx = ContextUtils.getContext();
            final Boolean forPreparedStatement = ctx.get("$exp.forPreparedStatement", Boolean.class);
            if (forPreparedStatement != null && forPreparedStatement && str2.startsWith(":")) {
                sb.append(str2);
                final HashMap<String, Object> parameters = ctx.get("$exp.statementParameters",HashMap.class);
                final String key = str2.substring(1);
                final String val = ConversionUtils.convert(parameters.get(key), String.class);
                if (!StringUtils.endsWith((CharSequence)val, (CharSequence)"%")) {
                    parameters.put(key, val + "%");
                }
            }
            else {
                if (!str2.startsWith("'")) {
                    sb.append("'");
                }
                if (str2.endsWith("'")) {
                    str2 = str2.substring(0, str2.length() - 1);
                }
                sb.append(str2);
                if (!StringUtils.contains((CharSequence)str2, (CharSequence)"%")) {
                    sb.append("%");
                }
                sb.append("'");
            }
        }
        else {
            str2 = ConversionUtils.convert(lso, String.class);
            sb.append("'").append(str2);
            if (!StringUtils.contains((CharSequence)str2, (CharSequence)"%")) {
                sb.append("%");
            }
            sb.append("'");
        }
        return sb.toString();
    }
}
