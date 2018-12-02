// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

public class ExpressionSet
{
    private String name;
    private HashMap<String, Expression> exprs;
    
    public ExpressionSet() {
        this.exprs = new HashMap<String, Expression>();
    }
    
    public void setName(final String nm) {
        this.name = nm;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setExpressions(final List<Expression> exprs) {
        for (final Expression expr : exprs) {
            this.addExpression(expr.getName(), expr);
        }
    }
    
    public void addExpression(final String nm, final Expression expr) {
        this.exprs.put(nm, expr);
    }
    
    public void register(final String nm, final Expression expr) {
        this.exprs.put(nm, expr);
    }
    
    public Expression getExpression(final String nm) {
        if (this.exprs.containsKey(nm)) {
            return this.exprs.get(nm);
        }
        return null;
    }
}
