// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.list;

import org.takeback.core.accredit.result.ConditionResult;
import org.dom4j.Element;
import org.takeback.core.accredit.result.AuthorizeResult;
import java.util.HashMap;
import java.io.Serializable;

public class WhiteList implements AccreditList, Serializable
{
    private static final long serialVersionUID = 467436151043085270L;
    HashMap<String, Object> list;
    
    public WhiteList() {
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
            r = AuthorizeResult.PositiveResult;
        }
        else if (this.list.containsKey("$others$")) {
            final Element el = this.list.get("$others$");
            final String acValue = el.attributeValue("acValue", "");
            if (acValue.length() > 0) {
                final ConditionResult cr = new ConditionResult();
                cr.setAuthorizeValue(acValue);
                r = cr;
            }
            else {
                r = AuthorizeResult.PositiveResult;
            }
        }
        else {
            r = AuthorizeResult.NegativeResult;
        }
        r.setContextList(this);
        return r;
    }
    
    @Override
    public HashMap<String, Object> containers() {
        return this.list;
    }
}
