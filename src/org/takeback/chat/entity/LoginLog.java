// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_loginLog")
public class LoginLog
{
    private String id;
    private Integer userId;
    private String userName;
    private String country;
    private String province;
    private String city;
    private String area;
    private String ip;
    private String realIp;
    private Date loginTime;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "ip")
    public String getIp() {
        return this.ip;
    }
    
    public void setIp(final String ip) {
        this.ip = ip;
    }
    
    @Basic
    @Column(name = "loginTime")
    public Date getLoginTime() {
        return this.loginTime;
    }
    
    public void setLoginTime(final Date loginTime) {
        this.loginTime = loginTime;
    }
    
    @Basic
    @Column(name = "realIp")
    public String getRealIp() {
        return this.realIp;
    }
    
    public void setRealIp(final String realIp) {
        this.realIp = realIp;
    }
    
    @Basic
    @Column(name = "userId")
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Integer userId) {
        this.userId = userId;
    }
    
    @Basic
    @Column(name = "area")
    public String getArea() {
        return this.area;
    }
    
    public void setArea(final String area) {
        this.area = area;
    }
    
    @Basic
    @Column(name = "city")
    public String getCity() {
        return this.city;
    }
    
    public void setCity(final String city) {
        this.city = city;
    }
    
    @Basic
    @Column(name = "province")
    public String getProvince() {
        return this.province;
    }
    
    public void setProvince(final String province) {
        this.province = province;
    }
    
    @Basic
    @Column(name = "userName")
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    @Basic
    @Column(name = "country")
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(final String country) {
        this.country = country;
    }
}
