// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.result;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.takeback.core.accredit.condition.Condition;
import java.util.HashMap;
import org.takeback.core.accredit.list.AccreditList;

public class ConditionResult implements AuthorizeResult
{
    AccreditList contextList;
    HashMap<String, Condition> cnds;
    String acValue;
    
    public ConditionResult() {
        this.cnds = new HashMap<String, Condition>();
        this.acValue = "";
    }
    
    @Override
    public void setContextList(final AccreditList list) {
        this.contextList = list;
    }
    
    @Override
    public AccreditList getContextList() {
        return this.contextList;
    }
    
    public void addCondition(final Condition cd) {
        this.cnds.put(cd.getQueryType(), cd);
    }
    
    public void setAuthorizeValue(final String acValue) {
        this.acValue = acValue;
    }
    
    public void setAllConditions(final List<Condition> cds) {
        for (final Condition cd : cds) {
            this.addCondition(cd);
        }
    }
    
    @Override
    public List<Condition> getAllConditions() {
        final List<Condition> ls = new ArrayList<Condition>();
        ls.addAll(this.cnds.values());
        return ls;
    }
    
    @Override
    public int conditionCount() {
        return this.cnds.size();
    }
    
    @Override
    public String getAuthorizeValue() {
        return this.acValue;
    }
    
    @Override
    public Condition getCondition(final String target) {
        return this.cnds.get(target);
    }
    
    @Override
    public int getResult() {
        return 2;
    }
    
    @Override
    public AuthorizeResult unite(final AuthorizeResult cr) {
        if (cr == null || cr.getResult() != 2) {
            return this;
        }
        if (cr.conditionCount() > 0) {
            this.setAllConditions(cr.getAllConditions());
        }
        final int i1 = Integer.valueOf(this.acValue, 2);
        final int i2 = Integer.valueOf(cr.getAuthorizeValue(), 2);
        this.acValue = Integer.toBinaryString(i1 | i2);
        return this;
    }
}
