// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Transient;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import java.util.Date;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_exchangeLog")
public class PubExchangeLog
{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    String shopId;
    @Column
    String shopName;
    @Column
    Double money;
    @Column
    Integer uid;
    @Column
    String nickName;
    @Column
    String name;
    @Column
    String address;
    @Column
    String mobile;
    @Column
    Date exchangeTime;
    @Column
    String status;
    @Column
    String admin;
    @Column
    Date dealTime;
    @Column
    String dealInfo;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getShopId() {
        return this.shopId;
    }
    
    public void setShopId(final String shopId) {
        this.shopId = shopId;
    }
    
    public String getShopName() {
        return this.shopName;
    }
    
    public void setShopName(final String shopName) {
        this.shopName = shopName;
    }
    
    public Double getMoney() {
        return this.money;
    }
    
    public void setMoney(final Double money) {
        this.money = money;
    }
    
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    public String getNickName() {
        return this.nickName;
    }
    
    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(final String address) {
        this.address = address;
    }
    
    public String getMobile() {
        return this.mobile;
    }
    
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }
    
    public Date getExchangeTime() {
        return this.exchangeTime;
    }
    
    public void setExchangeTime(final Date exchangeTime) {
        this.exchangeTime = exchangeTime;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public String getAdmin() {
        return this.admin;
    }
    
    public void setAdmin(final String admin) {
        this.admin = admin;
    }
    
    public Date getDealTime() {
        return this.dealTime;
    }
    
    public void setDealTime(final Date dealTime) {
        this.dealTime = dealTime;
    }
    
    public String getDealInfo() {
        return this.dealInfo;
    }
    
    public void setDealInfo(final String dealInfo) {
        this.dealInfo = dealInfo;
    }
    
    @Transient
    public String getStatusText() {
        return DictionaryController.instance().get("dic.chat.dealStatus").getText(this.status);
    }
}
