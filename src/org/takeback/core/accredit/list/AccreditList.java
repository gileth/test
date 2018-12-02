// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.list;

import java.util.HashMap;
import org.takeback.core.accredit.result.AuthorizeResult;

public interface AccreditList
{
    void add(final String p0, final Object p1);
    
    AuthorizeResult authorize(final String p0);
    
    HashMap<String, Object> containers();
}
