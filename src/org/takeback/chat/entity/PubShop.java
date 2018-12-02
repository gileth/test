// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.util.Date;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_shop")
public class PubShop
{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    String name;
    @Column
    String summary;
    @Column
    String detail;
    @Column
    String listImg;
    @Column
    String img1;
    @Column
    String img2;
    @Column
    String img3;
    @Column
    Double money;
    @Column
    Integer exchanged;
    @Column
    Integer sortNum;
    @Column
    Integer storage;
    @Column
    String createUser;
    @Column
    Date createDate;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getSummary() {
        return this.summary;
    }
    
    public void setSummary(final String summary) {
        this.summary = summary;
    }
    
    public String getDetail() {
        return this.detail;
    }
    
    public void setDetail(final String detail) {
        this.detail = detail;
    }
    
    public String getListImg() {
        return this.listImg;
    }
    
    public void setListImg(final String listImg) {
        this.listImg = listImg;
    }
    
    public String getImg1() {
        return this.img1;
    }
    
    public void setImg1(final String img1) {
        this.img1 = img1;
    }
    
    public String getImg2() {
        return this.img2;
    }
    
    public void setImg2(final String img2) {
        this.img2 = img2;
    }
    
    public String getImg3() {
        return this.img3;
    }
    
    public void setImg3(final String img3) {
        this.img3 = img3;
    }
    
    public Double getMoney() {
        return this.money;
    }
    
    public void setMoney(final Double money) {
        this.money = money;
    }
    
    public Integer getStorage() {
        return this.storage;
    }
    
    public void setStorage(final Integer storage) {
        this.storage = storage;
    }
    
    public String getCreateUser() {
        return this.createUser;
    }
    
    public void setCreateUser(final String createUser) {
        this.createUser = createUser;
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }
    
    public Integer getExchanged() {
        return this.exchanged;
    }
    
    public void setExchanged(final Integer exchanged) {
        this.exchanged = exchanged;
    }
}
