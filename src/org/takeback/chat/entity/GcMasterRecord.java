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
@Table(name = "gc_master")
public class GcMasterRecord
{
    private Integer id;
    private Integer uid;
    private String userId;
    private double freeze;
    private double restBetable;
    private double userInout;
    private double addBack;
    private Date openTime;
    private String status;
    private String roomId;
    
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
    @Column(name = "restBetable")
    public double getRestBetable() {
        return this.restBetable;
    }
    
    public void setRestBetable(final double restBetable) {
        this.restBetable = restBetable;
    }
    
    @Basic
    @Column(name = "roomId")
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
}
