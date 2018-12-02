// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.verification.message;

import java.util.List;
import org.dom4j.Document;
import org.springframework.core.io.Resource;
import org.dom4j.Element;
import org.takeback.util.xml.XMLHelper;
import org.takeback.core.resource.ResourceCenter;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Iterator;
import org.takeback.util.params.ParamUtils;
import java.util.Map;

public class SmsTemplates
{
    public static final String TEMPLATE_REGISTER_CODE = "1";
    public static final String TEMPLATE_FIND_PSW_CODE = "2";
    public static final String TEMPLATE_FIND_PAYPSW_CODE = "3";
    private static Map<String, String> smsStore;
    
    public static String getTemplate(final String tpl) {
        return getTemplate(tpl, null);
    }
    
    public static String getTemplate(final String tpl, final Map<String, String> params) {
        if (SmsTemplates.smsStore.containsKey(tpl)) {
            String t = SmsTemplates.smsStore.get(tpl);
            if (params != null) {
                for (final String p : params.keySet()) {
                    t = t.replace("#" + p + "#", String.valueOf(params.get(p)));
                }
            }
            return t.replace("#app#", ParamUtils.getParamSafe("NAME")).replace("#company#", ParamUtils.getParamSafe("NAME")).replace("#telphone#", ParamUtils.getParamSafe("TELPHONE"));
        }
        throw new IllegalArgumentException(String.format("sms template %s not exists.", tpl));
    }
    
    public static void reload() {
        SmsTemplates.smsStore = new HashMap<String,String>();
        try {
            final Resource resource = ResourceCenter.load("sms.xml");
            final Document doc = XMLHelper.getDocument(resource.getInputStream());
            final Element root = doc.getRootElement();
            final List<Element> tpls = (List<Element>)root.elements("tpl");
            for (final Element tpl : tpls) {
                SmsTemplates.smsStore.put(tpl.attributeValue("id"), tpl.getText());
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("load sms.xml failed.", e);
        }
    }
    
    static {
        reload();
    }
}
