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
@Table(name = "pc_gamelog")
public class PcGameLog
{
    private Integer id;
    private Integer num;
    private String betType;
    private String bet;
    private String luckyNumber;
    private Integer uid;
    private String userId;
    private Integer parentId;
    private double freeze;
    private double bonus;
    private double userInout;
    private double addBack;
    private double backMoney;
    private Date betTime;
    private Date openTime;
    private String status;
    
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
    @Column(name = "backMoney")
    public double getBackMoney() {
        return this.backMoney;
    }
    
    public void setBackMoney(final double backMoney) {
        this.backMoney = backMoney;
    }
    
    @Basic
    @Column(name = "bet")
    public String getBet() {
        return this.bet;
    }
    
    public void setBet(final String bet) {
        this.bet = bet;
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
    @Column(name = "luckyNumber")
    public String getLuckyNumber() {
        return this.luckyNumber;
    }
    
    public void setLuckyNumber(final String luckyNumber) {
        this.luckyNumber = luckyNumber;
    }
    
    @Basic
    @Column(name = "num")
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
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
    @Column(name = "parentId")
    public Integer getParentId() {
        return this.parentId;
    }
    
    public void setParentId(final Integer parentId) {
        this.parentId = parentId;
    }
}
