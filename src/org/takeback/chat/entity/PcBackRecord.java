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
@Table(name = "pc_back")
public class PcBackRecord
{
    private Integer id;
    private double money;
    private Date backDate;
    private double userInout;
    private Integer uid;
    private String userId;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "money")
    public double getMoney() {
        return this.money;
    }
    
    public void setMoney(final double money) {
        this.money = money;
    }
    
    @Basic
    @Column(name = "backDate")
    public Date getBackDate() {
        return this.backDate;
    }
    
    public void setBackDate(final Date backDate) {
        this.backDate = backDate;
    }
    
    @Basic
    @Column(name = "userInout")
    public double getUserInout() {
        return this.userInout;
    }
    
    public void setUserInout(final double userInout) {
        this.userInout = userInout;
    }
    
    @Basic
    @Column(name = "uid")
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    @Basic
    @Column(name = "userId")
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
}
