// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.organ;

import org.takeback.util.context.ContextUtils;
import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Set;
import java.util.Map;
import org.takeback.core.controller.support.AbstractConfigurable;

public class Organization extends AbstractConfigurable
{
    private static final long serialVersionUID = 5344390118571868091L;
    private Map<String, Organization> children;
    private Set<String> installedApps;
    private Organization parent;
    private Set<String> roles;
    private int deep;
    private String name;
    private String type;
    private String ref;
    private String pyCode;
    
    public Organization() {
        this.children = (Map<String, Organization>)Maps.newLinkedHashMap();
        this.installedApps = (Set<String>)Sets.newLinkedHashSet();
        this.roles = (Set<String>)Sets.newHashSet();
    }
    
    public void appendChild(final Organization unit) {
        unit.setParent(this);
        this.children.put(unit.getId(), unit);
    }
    
    public Organization getChild(final String id) {
        if (StringUtils.isEmpty((CharSequence)id)) {
            return null;
        }
        if (!id.contains(".")) {
            return this.children.get(id);
        }
        final String[] oid = id.split("\\.");
        Organization organ = this;
        for (int i = 0; i < oid.length; ++i) {
            final Organization o = organ.getChild(oid[i]);
            if (o == null) {
                return null;
            }
            organ = o;
        }
        return organ;
    }
    
    public void addRoleId(final String id) {
        this.roles.add(id);
    }
    
    @JsonIgnore
    public Collection<Organization> getChildren() {
        if (this.deep >= this.getRequestDeep()) {
            return null;
        }
        final Collection<Organization> c = this.children.values();
        if (c.isEmpty()) {
            return null;
        }
        return c;
    }
    
    public void addInstalledApp(final String id) {
        this.installedApps.add(id);
    }
    
    public Set<String> installedApps() {
        return Collections.unmodifiableSet((Set<? extends String>)this.installedApps);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getRef() {
        return this.ref;
    }
    
    public void setRef(final String ref) {
        this.ref = ref;
    }
    
    public String getPyCode() {
        return this.pyCode;
    }
    
    public void setPyCode(final String pyCode) {
        this.pyCode = pyCode;
    }
    
    @JsonIgnore
    public Organization getParent() {
        return this.parent;
    }
    
    public String getParentId() {
        if (this.parent != null) {
            return this.parent.getId();
        }
        return null;
    }
    
    private void setParent(final Organization parent) {
        this.parent = parent;
        this.deep = parent.deep() + 1;
    }
    
    protected int getRequestDeep() {
        if (ContextUtils.hasKey("$requestUnitDeep")) {
            return (int)ContextUtils.get("$requestUnitDeep");
        }
        return Integer.MAX_VALUE;
    }
    
    protected int deep() {
        return this.deep;
    }
}
