// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary;

import org.takeback.core.dictionary.support.XMLDictionary;
import org.takeback.util.converter.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import java.util.HashMap;
import org.dom4j.Element;
import org.takeback.core.controller.Configurable;
import org.takeback.core.controller.support.AbstractConfigurableLoader;

public class DictionaryLocalLoader extends AbstractConfigurableLoader<Dictionary>
{
    private static final String DEFAULT_DIC_PACKAGE = "org.takeback.core.dictionary.support.";
    
    public DictionaryLocalLoader() {
        this.postfix = ".dic";
    }
    
    private static void setupProperties(final Configurable o, final Element el) {
        final List<Element> ls = (List<Element>)el.selectNodes("properties/p");
        for (final Element p : ls) {
            final String nm = p.attributeValue("name");
            final List<Attribute> attrs = (List<Attribute>)p.attributes();
            if (attrs.size() > 1) {
                final Map<String, Object> map = new HashMap<String, Object>();
                for (final Attribute attr : attrs) {
                    map.put(attr.getName(), attr.getValue());
                }
                o.setProperty(nm, map);
            }
            else {
                final String v = p.getTextTrim();
                o.setProperty(nm, v);
            }
        }
    }
    
    public static Dictionary parseDocument(final String id, final Document doc, final long lastModi) {
        final Element root = doc.getRootElement();
        if (root == null) {
            return null;
        }
        String className = root.attributeValue("class", "XMLDictionary");
        try {
            if (!className.contains(".")) {
                className = StringUtils.join((Object[])new String[] { "org.takeback.core.dictionary.support.", className });
            }
            final Class<Dictionary> clz = (Class<Dictionary>)Class.forName(className);
            final Dictionary dic = ConversionUtils.convert(root, clz);
            dic.setId(id);
            dic.setLastModify(lastModi);
            setupProperties(dic, root);
            final List<Element> els = (List<Element>)root.selectNodes("//item");
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
                dic.addItem(item);
            }
            if (dic instanceof XMLDictionary) {
                ((XMLDictionary)dic).setDefineDoc(doc);
            }
            dic.init();
            return dic;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Dictionary createInstanceFormDoc(final String id, final Document doc, final long lastModi) {
        return parseDocument(id, doc, lastModi);
    }
}
