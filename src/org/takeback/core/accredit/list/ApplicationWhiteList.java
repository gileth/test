// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.list;

import org.takeback.core.accredit.result.AuthorizeResult;
import java.util.List;
import org.dom4j.Element;
import java.util.HashMap;

public class ApplicationWhiteList extends WhiteList
{
    private static final long serialVersionUID = -2788057587279673397L;
    HashMap<String, AccreditList> lss;
    
    public ApplicationWhiteList() {
        this.lss = new HashMap<String, AccreditList>();
    }
    
    @Override
    public void add(final String id, final Object ctx) {
        super.add(id, ctx);
        this.add(id, (Element)ctx);
    }
    
    public void add(final String id, final Element ctx) {
        final int n = ctx.elements().size();
        if (n > 0) {
            final String acType = ctx.attributeValue("acType", "whitelist");
            AccreditList mls;
            if (acType.equals("whitelist")) {
                mls = new ModuleWhiteList();
            }
            else {
                mls = new BlackList();
            }
            this.lss.put(id, mls);
            final List<Element> els = (List<Element>)ctx.elements();
            for (int i = 0; i < n; ++i) {
                final Element el = els.get(i);
                if (el.getName().equals("others")) {
                    mls.add("$others$", el);
                }
                else {
                    final String moduleId = el.attributeValue("id");
                    mls.add(moduleId, el);
                }
            }
        }
    }
    
    @Override
    public AuthorizeResult authorize(String id) {
        final int p = id.indexOf("/");
        String itemId = "";
        if (p != -1) {
            itemId = id.substring(p + 1);
            id = id.substring(0, p);
        }
        if (this.list.containsKey(id)) {
            if (itemId.length() == 0) {
                return AuthorizeResult.PositiveResult;
            }
            if (this.lss.containsKey(id)) {
                final AccreditList mls = this.lss.get(id);
                return mls.authorize(itemId);
            }
            return AuthorizeResult.NegativeResult;
        }
        else {
            if (this.list.containsKey("$others$")) {
                return AuthorizeResult.PositiveResult;
            }
            return AuthorizeResult.NegativeResult;
        }
    }
}
