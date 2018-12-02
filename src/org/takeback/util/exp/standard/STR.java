// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.standard;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.takeback.util.exp.Expression;

public class STR extends Expression
{
    public STR() {
        this.name = "s";
    }
    
    @Override
    public Object run(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        try {
            return ConversionUtils.convert(ls.get(1), String.class);
        }
        catch (Exception e) {
            throw new ExprException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final List<?> ls, final ExpressionProcessor processor) throws ExprException {
        return "'" + this.run(ls, processor) + "'";
    }
}
