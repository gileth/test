// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit;

import org.takeback.core.accredit.result.ConditionResult;
import org.takeback.core.accredit.result.AuthorizeResult;
import org.takeback.core.accredit.list.WhiteList;
import org.takeback.core.accredit.list.BlackList;
import java.util.Iterator;
import org.takeback.core.accredit.condition.ConditionFactory;
import java.util.ArrayList;
import org.dom4j.Element;
import org.takeback.core.accredit.list.AccreditList;
import java.util.HashMap;
import org.takeback.core.accredit.condition.Condition;
import java.util.List;
import java.io.Serializable;

public class AccreditStore implements Serializable
{
    private static final long serialVersionUID = 900088790085008371L;
    List<Condition> cds;
    HashMap<String, AccreditStoreItem> items;
    private AccreditList ls;
    private String acValue;
    
    public AccreditStore(final Element define) {
        this.cds = new ArrayList<Condition>();
        this.items = new HashMap<String, AccreditStoreItem>();
        this.acValue = "0000";
        if (define == null) {
            return;
        }
        this.acValue = define.attributeValue("acValue", "1111");
        final Element it = define.element("items");
        if (it != null) {
            final String acType = it.attributeValue("acType", "whitelist");
            if (acType.equals("whitelist")) {
                this.initWhiteList(it);
            }
            else {
                this.initBlackList(it);
            }
        }
        final List<Element> els = (List<Element>)define.selectNodes("conditions/condition");
        for (final Element el : els) {
            final Condition cd = ConditionFactory.createCondition(el);
            this.cds.add(cd);
        }
    }
    
    private void initBlackList(final Element parent) {
        this.ls = new BlackList();
        final List<Element> els = (List<Element>)parent.elements();
        for (final Element el : els) {
            this.ls.add(el.attributeValue("id"), el);
        }
    }
    
    private void initWhiteList(final Element parent) {
        this.ls = new WhiteList();
        final List<Element> els = (List<Element>)parent.elements();
        for (final Element el : els) {
            if (el.getName().equals("others")) {
                this.ls.add("$others$", el);
            }
            else {
                final String id = el.attributeValue("id");
                this.ls.add(id, el);
                this.items.put(id, new AccreditStoreItem(el));
            }
        }
    }
    
    public AuthorizeResult authorize(final String id) {
        if (this.ls == null) {
            return AuthorizeResult.NegativeResult;
        }
        final AuthorizeResult r = this.ls.authorize(id);
        r.setContextList(this.ls);
        if (r.getResult() != 1) {
            return r;
        }
        final AccreditStoreItem item = this.items.get(id);
        if (item == null) {
            return r;
        }
        return item.getResult();
    }
    
    public AuthorizeResult getResult() {
        final ConditionResult r = new ConditionResult();
        r.setAuthorizeValue(this.acValue);
        r.setAllConditions(this.cds);
        return r;
    }
}
