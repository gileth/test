// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.user;

import org.takeback.util.context.ContextUtils;
import org.takeback.util.converter.ConversionUtils;
import com.google.common.collect.Maps;
import org.takeback.core.organ.OrganController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.organ.Organization;
import org.takeback.core.role.Role;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class UserRoleToken implements Serializable
{
    private static final long serialVersionUID = 258173847713519333L;
    private Map<String, Object> properties;
    private Long id;
    private String userid;
    private String roleid;
    private String organid;
    
    public Long getId() {
        return this.id;
    }
    
    public String getDisplayName() {
        final StringBuilder sb = new StringBuilder(String.valueOf(this.id));
        sb.append("-").append(this.getUsername()).append("-").append(this.getRolename()).append("-").append(this.getOrganname());
        return sb.toString();
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public String getUserid() {
        return this.userid;
    }
    
    public void setUserid(final String userid) {
        this.userid = userid;
    }
    
    public String getRoleid() {
        return this.roleid;
    }
    
    public void setRoleid(final String roleid) {
        this.roleid = roleid;
    }
    
    public String getOrganid() {
        return this.organid;
    }
    
    public void setOrganid(final String organid) {
        this.organid = organid;
    }
    
    public String getUsername() {
        final User user = this.getUser();
        if (user != null) {
            return user.getName();
        }
        return null;
    }
    
    public Timestamp getLastLoginTime() {
        final User user = this.getUser();
        if (user != null) {
            return user.getLastsignintime();
        }
        return null;
    }
    
    public String getLastLoginIP() {
        final User user = this.getUser();
        if (user != null) {
            return user.getLastsigninip();
        }
        return null;
    }
    
    public String getRolename() {
        final Role role = this.getRole();
        if (role != null) {
            return role.getName();
        }
        return null;
    }
    
    public String getOrganname() {
        final Organization organ = this.getOrgan();
        if (organ != null) {
            return organ.getName();
        }
        return null;
    }
    
    @JsonIgnore
    public User getUser() {
        if (StringUtils.isEmpty((CharSequence)this.userid)) {
            return null;
        }
        return AccountCenter.getUser(this.userid);
    }
    
    @JsonIgnore
    public Role getRole() {
        if (StringUtils.isEmpty((CharSequence)this.roleid)) {
            return null;
        }
        return AccountCenter.getRole(this.roleid);
    }
    
    @JsonIgnore
    public Organization getOrgan() {
        if (StringUtils.isEmpty((CharSequence)this.organid)) {
            return null;
        }
        return OrganController.getRoot().getChild(this.organid);
    }
    
    public void setProperty(final String nm, final Object v) {
        if (this.properties == null) {
            this.properties = new HashMap<String,Object>();
        }
        this.properties.put(nm, v);
    }
    
    public Object getProperty(final String nm) {
        return this.getProperty(nm, false);
    }
    
    public Object getProperty(final String nm, final boolean inherit) {
        Object val = null;
        if (this.properties != null) {
            val = this.properties.get(nm);
            if (!inherit) {
                return val;
            }
        }
        if (val == null) {
            val = AccountCenter.getUser(this.userid).getProperty(nm);
        }
        if (val == null) {
            val = AccountCenter.getRole(this.roleid).getProperty(nm);
        }
        if (val == null) {
            val = OrganController.instance().get(this.organid).getProperty(nm);
        }
        return val;
    }
    
    public <T> T getProperty(final String nm, final Class<T> targetType) {
        return ConversionUtils.convert(this.getProperty(nm), targetType);
    }
    
    public <T> T getProperty(final String nm, final Class<T> targetType, final boolean inherit) {
        return ConversionUtils.convert(this.getProperty(nm, inherit), targetType);
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && !o.getClass().equals(this.getClass()) && o.hashCode() == this.hashCode();
    }
    
    @JsonIgnore
    public static UserRoleToken getCurrent() {
        final UserRoleToken urt = ContextUtils.get("$urt", UserRoleToken.class);
        if (urt == null) {
            throw new IllegalStateException("[UserRoleToken] not exist in thread context");
        }
        return urt;
    }
}
