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
@Table(name = "pub_manualmoney")
public class ManualMoneyLog
{
    private Integer id;
    private Integer userId;
    private String userIdText;
    private Double money;
    private String des;
    private String operator;
    private Date createTime;
    
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
    @Column(name = "userId", nullable = false)
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Integer userId) {
        this.userId = userId;
    }
    
    @Basic
    @Column(name = "userIdText", nullable = false)
    public String getUserIdText() {
        return this.userIdText;
    }
    
    public void setUserIdText(final String userIdText) {
        this.userIdText = userIdText;
    }
    
    @Basic
    @Column(name = "money", nullable = false)
    public Double getMoney() {
        return this.money;
    }
    
    public void setMoney(final Double money) {
        this.money = money;
    }
    
    @Basic
    @Column(name = "des", nullable = false)
    public String getDes() {
        return this.des;
    }
    
    public void setDes(final String des) {
        this.des = des;
    }
    
    @Basic
    @Column(name = "operator", nullable = false)
    public String getOperator() {
        return this.operator;
    }
    
    public void setOperator(final String operator) {
        this.operator = operator;
    }
    
    @Basic
    @Column(name = "createTime", nullable = false)
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
}
