// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit.condition;

import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import org.dom4j.Element;
import java.util.HashMap;
import org.slf4j.Logger;

public class ConditionFactory
{
    private static final Logger LOGGER;
    private static HashMap<String, String> cls;
    
    public static Condition createCondition(final Element define) {
        if (define == null) {
            return null;
        }
        try {
            final Condition cnd = (Condition)Class.forName(ConditionFactory.cls.get(define.attributeValue("type", "exp"))).newInstance();
            cnd.setDefine(define);
            return cnd;
        }
        catch (Exception e) {
            ConditionFactory.LOGGER.error("condition init failed:", (Throwable)e);
            return null;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ConditionFactory.class);
        (ConditionFactory.cls = (HashMap<String, String>)Maps.newHashMap()).put("exp", "org.takeback.core.accredit.condition.JsonExpCondition");
        ConditionFactory.cls.put("filter", "org.takeback.core.accredit.condition.JsonExpCondition");
        ConditionFactory.cls.put("notify", "org.takeback.core.accredit.condition.JsonExpCondition");
        ConditionFactory.cls.put("override", "org.takeback.core.accredit.condition.OverrideCondition");
        ConditionFactory.cls.put("limit", "org.takeback.core.accredit.condition.LimitCondition");
    }
}
