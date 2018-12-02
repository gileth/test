// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.role;

import org.takeback.core.controller.Configurable;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;
import org.takeback.util.converter.ConversionUtils;
import org.dom4j.Document;
import org.takeback.core.controller.support.AbstractConfigurableLoader;

public class RoleLocalLoader extends AbstractConfigurableLoader<Role>
{
    public RoleLocalLoader() {
        this.postfix = ".r";
    }
    
    @Override
    public Role createInstanceFormDoc(final String id, final Document doc, final long lastModi) {
        final Element root = doc.getRootElement();
        if (root == null) {
            return null;
        }
        try {
            final Role r = ConversionUtils.convert(root, Role.class);
            r.setId(id);
            r.setLastModify(lastModi);
            final List<Element> els = (List<Element>)root.selectNodes("accredit/*");
            for (final Element el : els) {
                r.initAccreditList(el);
            }
            this.setupProperties(r, root);
            return r;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private void setupProperties(final Role o, final Element el) {
        final List<Element> ls = (List<Element>)el.selectNodes("properties/p");
        for (final Element p : ls) {
            o.setProperty(p.attributeValue("name"), p.getTextTrim());
        }
    }
}
