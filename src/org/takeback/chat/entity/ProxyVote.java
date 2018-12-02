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
@Table(name = "pub_proxyVote")
public class ProxyVote
{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    Integer uid;
    @Column
    Double total;
    @Column
    Double vote;
    @Column
    String userId;
    @Column
    Date cacuDate;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    public Double getVote() {
        return this.vote;
    }
    
    public void setVote(final Double vote) {
        this.vote = vote;
    }
    
    public Date getCacuDate() {
        return this.cacuDate;
    }
    
    public void setCacuDate(final Date cacuDate) {
        this.cacuDate = cacuDate;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public Double getTotal() {
        return this.total;
    }
    
    public void setTotal(final Double total) {
        this.total = total;
    }
}
