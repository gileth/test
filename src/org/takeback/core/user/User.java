// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Maps;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.takeback.core.controller.support.AbstractConfigurable;

public class User extends AbstractConfigurable
{
    private static final long serialVersionUID = 3175037043404273987L;
    public static final String DEFAULT_AVATAR = "avatar/default.jpg";
    private Map<Long, UserRoleToken> roles;
    private String password;
    private String name;
    private String phonenumb;
    private String email;
    private String avatar;
    private Timestamp registertime;
    private Timestamp lastsignintime;
    private String lastsigninip;
    private String status;
    
    public User() {
        this.roles = new ConcurrentHashMap<Long, UserRoleToken>();
    }
    
    public String getPhonenumb() {
        return this.phonenumb;
    }
    
    public void setPhonenumb(final String phonenumb) {
        this.phonenumb = phonenumb;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public Timestamp getLastsignintime() {
        return this.lastsignintime;
    }
    
    public void setLastsignintime(final Timestamp lastsignintime) {
        this.lastsignintime = lastsignintime;
    }
    
    public String getLastsigninip() {
        return this.lastsigninip;
    }
    
    public void setLastsigninip(final String lastsigninip) {
        this.lastsigninip = lastsigninip;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public boolean validatePassword(final String pwd) {
        return (StringUtils.isEmpty((CharSequence)pwd) && StringUtils.isEmpty((CharSequence)this.password)) || this.password.equals(pwd);
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public Date getRegistertime() {
        return this.registertime;
    }
    
    public void setRegistertime(final Timestamp registertime) {
        this.registertime = registertime;
    }
    
    public String getAvatar() {
        if (StringUtils.isEmpty((CharSequence)this.avatar)) {
            return "avatar/default.jpg";
        }
        return this.avatar;
    }
    
    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }
    
    public void addUserRoleToken(final UserRoleToken ur) {
        this.roles.put(ur.getId(), ur);
    }
    
    public void removeUserRoleToken(final int id) {
        this.roles.remove(id);
    }
    
    public boolean hasUserRoleToken(final UserRoleToken ur) {
        return this.roles.containsValue(ur);
    }
    
    public boolean hasUserRoleToken(final Integer urId) {
        return this.roles.containsKey(urId);
    }
    
    public UserRoleToken getUserRoleToken(final long urId) {
        return this.roles.get(urId);
    }
    
    @JsonIgnore
    public Collection<UserRoleToken> getUserRoleTokens() {
        return this.roles.values();
    }
    
    @JsonIgnore
    public boolean isForbidden() {
        return !"1".equals(this.status);
    }
}
