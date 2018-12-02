// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.condition;

import java.util.Set;
import org.takeback.util.StringValueParser;
import org.takeback.util.context.Context;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import org.dom4j.Element;

public class OverrideCondition implements Condition
{
    Element define;
    HashMap<String, String> overrides;
    
    public OverrideCondition() {
        this.overrides = new HashMap<String, String>();
    }
    
    @Override
    public void setDefine(final Element define) {
        this.define = define;
        if (define == null) {
            return;
        }
        if (define.elements().size() == 0) {
            final String target = define.attributeValue("target");
            final String value = define.attributeValue("value", define.getText());
            this.overrides.put(target, value);
        }
        else {
            final List<Element> list = (List<Element>)define.elements();
            for (final Element o : list) {
                this.overrides.put(o.attributeValue("target"), o.attributeValue("value", o.getText()));
            }
        }
    }
    
    @Override
    public Element getDefine() {
        return this.define;
    }
    
    @Override
    public String getMessage() {
        return "";
    }
    
    @Override
    public String run(final Context ctx) {
        final Set<String> keys = this.overrides.keySet();
        for (final String target : keys) {
            final String v = StringValueParser.parse(this.overrides.get(target), String.class);
            ((HashMap<String, String>)ctx).put("cfg." + target, v);
        }
        return null;
    }
    
    @Override
    public HashMap<String, Object> data() {
        final HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("type", "override");
        return h;
    }
    
    @Override
    public String getQueryType() {
        return "override";
    }
    
    @Override
    public int type() {
        return 2;
    }
}
