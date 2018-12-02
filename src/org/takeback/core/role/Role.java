// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.role;

import org.takeback.core.accredit.result.NegativeResult;
import org.takeback.core.accredit.result.AuthorizeResult;
import org.takeback.core.accredit.list.BlackList;
import org.takeback.core.accredit.AccreditStore;
import org.takeback.core.accredit.list.StorgeWhiteList;
import java.util.Iterator;
import java.util.List;
import org.takeback.core.accredit.list.WhiteList;
import org.takeback.core.accredit.list.ApplicationWhiteList;
import org.dom4j.Element;
import org.takeback.core.user.AccountCenter;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.accredit.list.AccreditList;
import java.util.HashMap;
import org.takeback.core.controller.support.AbstractConfigurable;

public class Role extends AbstractConfigurable
{
    private static final long serialVersionUID = -2219302553517602005L;
    private String name;
    private String desc;
    private String type;
    private String parent;
    private HashMap<String, AccreditList> accredits;
    
    public Role() {
        this.accredits = new HashMap<String, AccreditList>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return this.desc;
    }
    
    public void setDescription(final String desc) {
        this.desc = desc;
    }
    
    public String getType() {
        return (this.type != null) ? this.type : "";
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public Role getParent() {
        if (!StringUtils.isEmpty((CharSequence)this.parent)) {
            return AccountCenter.getRole(this.parent);
        }
        return null;
    }
    
    public void setParent(final String parent) {
        this.parent = parent;
    }
    
    public void initAccreditList(final Element el) {
        final String acType = el.attributeValue("acType", "whitelist");
        if (acType.equals("whitelist")) {
            final String nm = el.getName();
            if (nm.equals("storage")) {
                this.initStorageWhiteList(el);
            }
            else if (nm.equals("apps")) {
                this.initAppsWhiteList(el);
            }
            else {
                this.initWhiteList(el);
            }
        }
        else {
            this.initBlackList(el);
        }
    }
    
    private void initAppsWhiteList(final Element parent) {
        final WhiteList White = new ApplicationWhiteList();
        final List<Element> items = (List<Element>)parent.elements();
        for (final Element el : items) {
            if (el.getName().equals("others")) {
                White.add("$others$", el);
            }
            else {
                White.add(el.attributeValue("id"), el);
            }
        }
        this.accredits.put(parent.getName(), White);
    }
    
    private void initStorageWhiteList(final Element parent) {
        final StorgeWhiteList White = new StorgeWhiteList();
        final List<Element> items = (List<Element>)parent.elements();
        for (final Element el : items) {
            if (el.getName().equals("others")) {
                White.add("$others$", el);
            }
            else {
                White.add(el.attributeValue("id"), new AccreditStore(el));
            }
        }
        this.accredits.put(parent.getName(), White);
    }
    
    private void initWhiteList(final Element parent) {
        final WhiteList White = new WhiteList();
        final List<Element> items = (List<Element>)parent.elements();
        for (final Element el : items) {
            if (el.getName().equals("others")) {
                White.add("$others$", el);
            }
            else {
                White.add(el.attributeValue("id"), el);
            }
        }
        this.accredits.put(parent.getName(), White);
    }
    
    private void initBlackList(final Element parent) {
        final BlackList black = new BlackList();
        final List<Element> items = (List<Element>)parent.elements();
        for (final Element el : items) {
            black.add(el.attributeValue("id"), el);
        }
        this.accredits.put(parent.getName(), black);
    }
    
    public AccreditList getAccreditList(final String name) {
        return this.accredits.get(name);
    }
    
    public AuthorizeResult authorize(final String name, final String id) {
        AuthorizeResult p = null;
        final Role parentRole = this.getParent();
        if (parentRole != null) {
            p = parentRole.authorize(name, id);
        }
        AuthorizeResult r = null;
        if (!this.accredits.containsKey(name)) {
            r = new NegativeResult();
            return r.unite(p);
        }
        final AccreditList ls = this.accredits.get(name);
        r = ls.authorize(id);
        return r.unite(p);
    }
}
