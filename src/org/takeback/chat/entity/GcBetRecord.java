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
@Table(name = "gc_bet")
public class GcBetRecord
{
    private Integer id;
    private String betType;
    private Integer uid;
    private String userId;
    private double money;
    private double freeze;
    private double bonus;
    private double userInout;
    private double addBack;
    private Date betTime;
    private Date openTime;
    private String status;
    private Integer masterRecordId;
    
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
    @Column(name = "addBack")
    public double getAddBack() {
        return this.addBack;
    }
    
    public void setAddBack(final double addBack) {
        this.addBack = addBack;
    }
    
    @Basic
    @Column(name = "betType")
    public String getBetType() {
        return this.betType;
    }
    
    public void setBetType(final String betType) {
        this.betType = betType;
    }
    
    @Basic
    @Column(name = "bonus")
    public double getBonus() {
        return this.bonus;
    }
    
    public void setBonus(final double bonus) {
        this.bonus = bonus;
    }
    
    @Basic
    @Column(name = "betTime")
    public Date getBetTime() {
        return this.betTime;
    }
    
    public void setBetTime(final Date betTime) {
        this.betTime = betTime;
    }
    
    @Basic
    @Column(name = "freeze")
    public double getFreeze() {
        return this.freeze;
    }
    
    public void setFreeze(final double freeze) {
        this.freeze = freeze;
    }
    
    @Basic
    @Column(name = "openTime")
    public Date getOpenTime() {
        return this.openTime;
    }
    
    public void setOpenTime(final Date openTime) {
        this.openTime = openTime;
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
    
    @Basic
    @Column(name = "userInout")
    public double getUserInout() {
        return this.userInout;
    }
    
    public void setUserInout(final double userInout) {
        this.userInout = userInout;
    }
    
    @Basic
    @Column(name = "status")
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
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
    @Column(name = "masterRecordId")
    public Integer getMasterRecordId() {
        return this.masterRecordId;
    }
    
    public void setMasterRecordId(final Integer masterRecordId) {
        this.masterRecordId = masterRecordId;
    }
}
