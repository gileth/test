// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp;

import java.util.HashMap;
import java.util.Map;

public class ExpressionContextBean
{
    private boolean forPreparedStatement;
    private Map<String, Object> statementParameters;
    
    public ExpressionContextBean() {
        this.forPreparedStatement = false;
    }
    
    public boolean isForPreparedStatement() {
        return this.forPreparedStatement;
    }
    
    public void setForPreparedStatement(final boolean forPreparedStatement) {
        this.forPreparedStatement = forPreparedStatement;
    }
    
    public void setParameter(final String nm, final Object val) {
        if (this.statementParameters == null) {
            this.statementParameters = new HashMap<String, Object>();
        }
        this.statementParameters.put(nm, val);
    }
    
    public Map<String, Object> getStatementParameters() {
        if (this.statementParameters == null) {
            this.statementParameters = new HashMap<String, Object>();
        }
        return this.statementParameters;
    }
    
    public void clearPatameters() {
        if (this.statementParameters != null) {
            this.statementParameters.clear();
        }
    }
}
