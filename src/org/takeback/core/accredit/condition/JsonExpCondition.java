// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.condition;

import org.takeback.util.exp.exception.ExprException;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.context.Context;
import org.takeback.util.JSONUtils;
import java.util.List;
import java.util.HashMap;
import org.dom4j.Element;

public class JsonExpCondition implements Condition
{
    Element define;
    HashMap<String, Object> data;
    String queryType;
    
    @Override
    public void setDefine(final Element define) {
        this.define = define;
        if (define == null) {
            return;
        }
        final Element exp = define.element("exp");
        final Element msg = define.element("errMsg");
        this.queryType = define.attributeValue("type");
        String expText = null;
        if (exp == null) {
            expText = define.attributeValue("exp", define.getText());
        }
        else {
            expText = exp.getText();
        }
        try {
            expText = expText.trim().replaceAll("'", "\"");
            final List<?> lsExp = JSONUtils.parse(expText, (Class<List<?>>)List.class);
            (this.data = new HashMap<String, Object>()).put("exp", lsExp);
            if (msg != null) {
                this.data.put("errMsg", msg.getText());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Element getDefine() {
        return this.define;
    }
    
    @Override
    public String getMessage() {
        return this.data.get("errMsg");
    }
    
    @Override
    public Object run(final Context ctx) {
        try {
            return ExpressionProcessor.instance().run(this.data.get("exp"));
        }
        catch (ExprException e) {
            return null;
        }
    }
    
    @Override
    public HashMap<String, Object> data() {
        return this.data;
    }
    
    @Override
    public String getQueryType() {
        return this.queryType;
    }
    
    @Override
    public int type() {
        return 1;
    }
}
