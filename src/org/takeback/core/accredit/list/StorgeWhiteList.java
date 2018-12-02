// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.list;

import org.dom4j.Element;
import org.takeback.core.accredit.result.ConditionResult;
import org.takeback.core.accredit.AccreditStore;
import org.takeback.core.accredit.result.AuthorizeResult;

public class StorgeWhiteList extends WhiteList
{
    private static final long serialVersionUID = -5231699451569983293L;
    
    @Override
    public AuthorizeResult authorize(String id) {
        final int p = id.indexOf("/");
        String itemId = "";
        if (p != -1) {
            itemId = id.substring(p + 1);
            id = id.substring(0, p);
        }
        AuthorizeResult r = null;
        if (this.list.containsKey(id)) {
            final AccreditStore acs = (AccreditStore) this.list.get(id);
            if (itemId.length() == 0) {
                r = acs.getResult();
            }
            else {
                r = acs.authorize(itemId);
            }
        }
        else if (this.list.containsKey("$others$")) {
            final ConditionResult cdr = new ConditionResult();
            final Element el = (Element) this.list.get("$others$");
            cdr.setAuthorizeValue(el.attributeValue("acValue", "1111"));
            cdr.setContextList(this);
            r = cdr;
        }
        else {
            r = AuthorizeResult.NegativeResult;
        }
        r.setContextList(this);
        return r;
    }
}
