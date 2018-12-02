// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.condition;

import java.util.HashMap;
import org.takeback.util.context.Context;
import org.dom4j.Element;
import java.io.Serializable;

public interface Condition extends Serializable
{
    public static final int EXP = 1;
    public static final int OVERRIDE = 2;
    public static final int LIMIT = 3;
    
    void setDefine(final Element p0);
    
    Object run(final Context p0);
    
    Element getDefine();
    
    String getMessage();
    
    HashMap<String, Object> data();
    
    int type();
    
    String getQueryType();
}
