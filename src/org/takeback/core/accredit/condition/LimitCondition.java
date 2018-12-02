// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.condition;

import java.util.HashMap;
import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.context.Context;
import org.takeback.util.JSONUtils;
import org.takeback.core.accredit.list.BlackList;
import org.takeback.core.accredit.list.WhiteList;
import java.util.List;
import org.takeback.core.accredit.list.AccreditList;
import org.dom4j.Element;

public class LimitCondition implements Condition
{
    Element define;
    AccreditList list;
    String value;
    List<?> exp;
    String listType;
    
    @Override
    public void setDefine(final Element define) {
        this.define = define;
        if (define == null) {
            return;
        }
        this.listType = define.attributeValue("list", "white");
        this.value = define.attributeValue("value");
        if (this.value != null) {
            if (this.listType.equals("white")) {
                this.list = new WhiteList();
            }
            else {
                this.list = new BlackList();
            }
            if (this.value.indexOf(",") > -1) {
                final String[] split;
                final String[] items = split = this.value.split(",");
                for (final String i : split) {
                    this.list.add(i, null);
                }
            }
            else {
                this.list.add(this.value, null);
            }
        }
        else {
            try {
                final String s = define.attributeValue("exp", define.getText());
                this.exp = JSONUtils.parse(s,List.class);
            }
            catch (Exception ex) {}
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
    public Object run(final Context ctx) {
        if (this.list != null) {
            final String v = (String)ctx.get("value");
            return this.list.authorize(v).getResult() != 0;
        }
        if (this.exp != null) {
            try {
                return ExpressionProcessor.instance().run(this.exp);
            }
            catch (ExprException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    
    @Override
    public HashMap<String, Object> data() {
        final HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("type", "limit");
        h.put("value", this.value);
        h.put("list", this.listType);
        h.put("exp", this.exp);
        return h;
    }
    
    @Override
    public String getQueryType() {
        return "limit";
    }
    
    @Override
    public int type() {
        return 3;
    }
}
