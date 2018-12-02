// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.controller.support;

import java.util.HashMap;
import org.takeback.util.JSONUtils;
import org.takeback.util.context.ContextUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import java.util.List;
import org.takeback.util.BeanUtils;
import org.dom4j.Element;
import org.dom4j.Document;
import org.springframework.core.io.Resource;
import org.takeback.util.xml.XMLHelper;
import org.takeback.core.resource.ResourceCenter;
import org.takeback.core.controller.ConfigurableLoader;
import org.takeback.core.controller.Configurable;

public abstract class AbstractConfigurableLoader<T extends Configurable> implements ConfigurableLoader<T>
{
    protected String postfix;
    
    public AbstractConfigurableLoader() {
        this.postfix = ".xml";
    }
    
    public void setPostfix(final String postfix) {
        this.postfix = postfix;
    }
    
    public String getPostfix() {
        return this.postfix;
    }
    
    @Override
    public T load(final String id) {
        final String path = id.replaceAll("\\.", "/") + this.postfix;
        try {
            final Resource r = ResourceCenter.load("classpath:", path);
            final Document doc = XMLHelper.getDocument(r.getInputStream());
            return this.createInstanceFormDoc(id, doc, r.lastModified());
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public abstract T createInstanceFormDoc(final String p0, final Document p1, final long p2);
    
    protected void setupProperties(final Object o, final Element el) {
        final List<Element> ls = (List<Element>)el.selectNodes("properties/p");
        try {
            for (final Element p : ls) {
                final Object value = this.parseToObject(p.getTextTrim());
                if (value == null) {
                    continue;
                }
                BeanUtils.setPropertyInMap(o, p.attributeValue("name"), value);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Object parseToObject(final String s) {
        if (StringUtils.isEmpty((CharSequence)s)) {
            return null;
        }
        Object v = null;
        switch (s.charAt(0)) {
            case '%': {
                v = ContextUtils.get(s.substring(1));
                break;
            }
            case '[': {
                v = JSONUtils.parse(s, List.class);
                break;
            }
            case '{': {
                v = JSONUtils.parse(s, HashMap.class);
                break;
            }
            default: {
                v = s;
                break;
            }
        }
        return v;
    }
}
