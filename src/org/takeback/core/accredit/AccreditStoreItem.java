// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit;

import org.takeback.core.accredit.result.ConditionResult;
import org.takeback.core.accredit.result.AuthorizeResult;
import java.util.Iterator;
import org.takeback.core.accredit.condition.ConditionFactory;
import java.util.ArrayList;
import org.dom4j.Element;
import org.takeback.core.accredit.condition.Condition;
import java.util.List;
import java.io.Serializable;

public class AccreditStoreItem implements Serializable
{
    private static final long serialVersionUID = -6747079386253280933L;
    List<Condition> cds;
    private String acValue;
    
    public AccreditStoreItem(final Element define) {
        this.cds = new ArrayList<Condition>();
        this.acValue = "0000";
        if (define == null) {
            return;
        }
        define.attributeValue("id");
        this.acValue = define.attributeValue("acValue", "1111");
        final List<Element> els = (List<Element>)define.elements("condition");
        for (final Element el : els) {
            final Condition cd = ConditionFactory.createCondition(el);
            this.cds.add(cd);
        }
    }
    
    public AuthorizeResult getResult() {
        final ConditionResult r = new ConditionResult();
        r.setAuthorizeValue(this.acValue);
        r.setAllConditions(this.cds);
        return r;
    }
}
