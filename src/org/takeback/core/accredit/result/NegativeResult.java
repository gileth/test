// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.result;

import java.util.List;
import org.takeback.core.accredit.condition.Condition;
import org.takeback.core.accredit.list.AccreditList;

public class NegativeResult implements AuthorizeResult
{
    AccreditList contextList;
    
    @Override
    public void setContextList(final AccreditList list) {
        this.contextList = list;
    }
    
    @Override
    public AccreditList getContextList() {
        return this.contextList;
    }
    
    @Override
    public int conditionCount() {
        return 0;
    }
    
    @Override
    public String getAuthorizeValue() {
        return "0000";
    }
    
    @Override
    public Condition getCondition(final String target) {
        return null;
    }
    
    @Override
    public int getResult() {
        return 0;
    }
    
    @Override
    public List<Condition> getAllConditions() {
        return null;
    }
    
    @Override
    public AuthorizeResult unite(final AuthorizeResult cr) {
        if (cr == null || this.getResult() > cr.getResult()) {
            return this;
        }
        return cr;
    }
    
    @Override
    public String toString() {
        return "false";
    }
}
