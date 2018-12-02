// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.result;

import java.util.List;
import org.takeback.core.accredit.condition.Condition;
import org.takeback.core.accredit.list.AccreditList;

public interface AuthorizeResult
{
    public static final int UNDERCONDITION = 2;
    public static final int YES = 1;
    public static final int NO = 0;
    public static final AuthorizeResult NegativeResult = new NegativeResult();
    public static final AuthorizeResult PositiveResult = new PositiveResult();
    
    void setContextList(final AccreditList p0);
    
    AccreditList getContextList();
    
    int getResult();
    
    int conditionCount();
    
    Condition getCondition(final String p0);
    
    List<Condition> getAllConditions();
    
    String getAuthorizeValue();
    
    AuthorizeResult unite(final AuthorizeResult p0);
}
