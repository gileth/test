// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_room_member")
public class GcRoomMember
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Basic
    @Column
    private String roomId;
    @Basic
    @Column
    private Double rate;
    @Basic
    @Column
    private Integer uid;
    @Basic
    @Column
    private String userId;
    @Basic
    @Column
    private String nickName;
    @Basic
    @Column
    private String isPartner;
    @Basic
    @Column
    private Date joinDate;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getNickName() {
        return this.nickName;
    }
    
    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }
    
    public String getIsPartner() {
        return this.isPartner;
    }
    
    public void setIsPartner(final String isPartner) {
        this.isPartner = isPartner;
    }
    
    public Date getJoinDate() {
        return this.joinDate;
    }
    
    public void setJoinDate(final Date joinDate) {
        this.joinDate = joinDate;
    }
    
    public Double getRate() {
        return this.rate;
    }
    
    public void setRate(final Double rate) {
        this.rate = rate;
    }
}
