// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary.support;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.core.dictionary.DictionaryItem;
import org.dom4j.Element;
import org.dom4j.Document;
import org.takeback.util.ApplicationContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class CustomDictionary extends XMLDictionary
{
    private static final long serialVersionUID = -29426695300041656L;
    private static final Logger LOGGER;
    private String className;
    private String method;
    private String bean;
    
    public String getBean() {
        return this.bean;
    }
    
    public void setBean(final String bean) {
        this.bean = bean;
    }
    
    public void setClassName(final String clazz) {
        this.className = clazz;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    @Override
    public void init() {
        try {
            Object obj = null;
            if (!StringUtils.isEmpty((CharSequence)this.bean)) {
                obj = ApplicationContextHolder.getBean(this.bean);
            }
            else {
                obj = Class.forName(this.className).newInstance();
            }
            final Method m = obj.getClass().getMethod(this.method, (Class<?>[])new Class[0]);
            final Document newDefineDoc = (Document)m.invoke(obj, new Object[0]);
            final List<Element> els = (List<Element>)newDefineDoc.getRootElement().selectNodes("//item");
            for (final Element el : els) {
                final DictionaryItem item = ConversionUtils.convert(el, DictionaryItem.class);
                if (el.elements("item").size() == 0 && !"false".equals(el.attributeValue("leaf"))) {
                    item.setLeaf(true);
                }
                final Element parent = el.getParent();
                if (parent != null) {
                    final String pKey = parent.attributeValue("key", "");
                    item.setProperty("parent", pKey);
                }
                this.addItem(item);
            }
            this.setDefineDoc(newDefineDoc);
        }
        catch (Exception e) {
            CustomDictionary.LOGGER.error("get custom dic[{}] for class[{}], method[{}] occur error.", new Object[] { this.id, this.className, this.method, e });
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CustomDictionary.class);
    }
}
