// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.list;

import org.takeback.core.accredit.result.AuthorizeResult;
import java.util.HashMap;
import java.io.Serializable;

public class BlackList implements AccreditList, Serializable
{
    private static final long serialVersionUID = 5815488500591951520L;
    private HashMap<String, Object> list;
    
    public BlackList() {
        this.list = new HashMap<String, Object>();
    }
    
    @Override
    public void add(final String id, final Object ctx) {
        this.list.put(id, ctx);
    }
    
    @Override
    public AuthorizeResult authorize(final String id) {
        AuthorizeResult r = null;
        if (this.list.containsKey(id)) {
            r = AuthorizeResult.NegativeResult;
        }
        else {
            r = AuthorizeResult.PositiveResult;
        }
        r.setContextList(this);
        return r;
    }
    
    @Override
    public HashMap<String, Object> containers() {
        return this.list;
    }
}
