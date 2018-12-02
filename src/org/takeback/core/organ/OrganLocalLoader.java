// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.organ;

import org.takeback.core.controller.Configurable;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;
import org.takeback.util.converter.ConversionUtils;
import org.dom4j.Document;
import org.takeback.core.controller.support.AbstractConfigurableLoader;

public class OrganLocalLoader extends AbstractConfigurableLoader<Organization>
{
    public OrganLocalLoader() {
        this.postfix = ".org";
    }
    
    @Override
    public Organization createInstanceFormDoc(final String id, final Document doc, final long lastModi) {
        final Element root = doc.getRootElement();
        if (root == null) {
            return null;
        }
        try {
            final Organization organ = ConversionUtils.convert(root, Organization.class);
            organ.setLastModify(lastModi);
            final List<Element> installedApps = (List<Element>)root.selectNodes("installedApps/app");
            for (final Element appEl : installedApps) {
                organ.addInstalledApp(appEl.attributeValue("id"));
            }
            this.parseChilds(organ, root);
            return organ;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private void parseChilds(final Organization parent, final Element el) {
        this.setupProperties(parent, el);
        final List<Element> children = (List<Element>)el.elements("unit");
        for (final Element u : children) {
            final Organization unit = ConversionUtils.convert(u, Organization.class);
            parent.appendChild(unit);
            this.parseChilds(unit, u);
        }
    }
    
    private void setupProperties(final Organization unit, final Element el) {
        final List<Element> ls = (List<Element>)el.selectNodes("properties/p");
        for (final Element p : ls) {
            unit.setProperty(p.attributeValue("name"), p.getTextTrim());
        }
    }
}
