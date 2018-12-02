// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import org.takeback.chat.utils.NumberUtil;
import java.util.Date;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_transfer")
public class TransferLog
{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    Integer fromUid;
    @Column
    String fromNickName;
    @Column
    Integer toUid;
    @Column
    String toNickName;
    @Column
    Double money;
    @Column
    Date transferDate;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getFromUid() {
        return this.fromUid;
    }
    
    public void setFromUid(final Integer fromUid) {
        this.fromUid = fromUid;
    }
    
    public String getFromNickName() {
        return this.fromNickName;
    }
    
    public void setFromNickName(final String fromNickName) {
        this.fromNickName = fromNickName;
    }
    
    public Integer getToUid() {
        return this.toUid;
    }
    
    public void setToUid(final Integer toUid) {
        this.toUid = toUid;
    }
    
    public String getToNickName() {
        return this.toNickName;
    }
    
    public void setToNickName(final String toNickName) {
        this.toNickName = toNickName;
    }
    
    public Double getMoney() {
        return NumberUtil.round(this.money);
    }
    
    public void setMoney(final Double money) {
        this.money = money;
    }
    
    public Date getTransferDate() {
        return this.transferDate;
    }
    
    public void setTransferDate(final Date transferDate) {
        this.transferDate = transferDate;
    }
}
