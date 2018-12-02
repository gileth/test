// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.app;

import org.takeback.core.controller.Configurable;
import org.springframework.core.io.Resource;
import org.dom4j.Attribute;
import org.takeback.util.xml.XMLHelper;
import org.takeback.core.resource.ResourceCenter;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.takeback.util.converter.ConversionUtils;
import org.dom4j.Document;
import org.takeback.core.controller.support.AbstractConfigurableLoader;

public class ApplicationLocalLoader extends AbstractConfigurableLoader<Application>
{
    public ApplicationLocalLoader() {
        this.postfix = ".app";
    }
    
    @Override
    public Application createInstanceFormDoc(final String id, final Document doc, final long lastModi) {
        final Element root = doc.getRootElement();
        if (root == null) {
            return null;
        }
        try {
            final Application app = ConversionUtils.convert(root, Application.class);
            app.setId(id);
            app.setLastModify(lastModi);
            this.setupProperties(app, root);
            this.parseChilds(app, root);
            return app;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private void parseChilds(final ApplicationNode appNode, final Element el) {
        List<Element> ls = (List<Element>)el.elements("catagory");
        for (Element catEl : ls) {
            final String ref = catEl.attributeValue("ref", "");
            if (!StringUtils.isEmpty((CharSequence)ref)) {
                catEl = this.loadRefNode(ref, catEl);
            }
            final Category category = ConversionUtils.convert(catEl, Category.class);
            appNode.appendChild(category);
            this.setupProperties(category, catEl);
            this.parseRefNode(category);
            this.parseChilds(category, catEl);
        }
        ls = (List<Element>)el.elements("module");
        for (Element modEl : ls) {
            final String ref = modEl.attributeValue("ref", "");
            if (!StringUtils.isEmpty((CharSequence)ref)) {
                modEl = this.loadRefNode(ref, modEl);
            }
            final Module mod = ConversionUtils.convert(modEl, Module.class);
            appNode.appendChild(mod);
            this.setupProperties(mod, modEl);
            this.parseRefNode(mod);
            this.parseChilds(mod, modEl);
        }
        ls = (List<Element>)el.elements("action");
        for (final Element actEl : ls) {
            final Action action = ConversionUtils.convert(actEl, Action.class);
            appNode.appendChild(action);
        }
    }
    
    private void parseRefNode(final ApplicationNode node) {
        ApplicationNode it = node.getParent();
        if (it != null) {
            if (it.hasRef() && !node.hasRef()) {
                node.setRef(it.getRef() + "/" + node.getId());
            }
            if (node.hasRef()) {
                while (it.getParent() != null) {
                    it = it.getParent();
                }
                ((Application)it).addRefItem(node);
            }
        }
    }
    
    private Element loadRefNode(final String ref, final Element el) {
        Element node = null;
        if (StringUtils.isEmpty((CharSequence)ref)) {
            return null;
        }
        final String[] nodes = ref.split("/");
        if (nodes.length > 1) {
            final String path = nodes[0].replaceAll("\\.", "/") + this.postfix;
            try {
                final Resource r = ResourceCenter.load("classpath:", path);
                final Document doc = XMLHelper.getDocument(r.getInputStream());
                node = doc.getRootElement();
                for (int i = 1; i < nodes.length; ++i) {
                    node = (Element)node.selectSingleNode("*[@id='" + nodes[i] + "']");
                    if (node == null) {
                        break;
                    }
                }
            }
            catch (Exception e) {
                return null;
            }
        }
        if (node == null) {
            return null;
        }
        for(int i=0;i< el.attributes().size();i++) {
        	Attribute att = (Attribute) el.attributes().get(i);
            node.addAttribute(att.getName(), att.getValue());
        }
        return node;
    }
    
    private void setupProperties(final ApplicationNode o, final Element el) {
        final List<Element> ls = (List<Element>)el.selectNodes("properties/p");
        for (final Element p : ls) {
            o.setProperty(p.attributeValue("name"), p.getTextTrim());
        }
    }
}
