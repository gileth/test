// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_valueControl")
public class ValueControlLog
{
    @Column
    @Id
    @GeneratedValue
    private String id;
    @Column
    private Integer uid;
    @Column
    private String nickName;
    @Column
    private String roomId;
    @Column
    private String roomName;
    @Column
    private Double val;
    @Column
    private Date createDate;
    @Column
    private String admin;
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
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
    
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    public String getRoomName() {
        return this.roomName;
    }
    
    public void setRoomName(final String roomName) {
        this.roomName = roomName;
    }
    
    public Double getVal() {
        return this.val;
    }
    
    public void setVal(final Double val) {
        this.val = val;
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }
    
    public String getAdmin() {
        return this.admin;
    }
    
    public void setAdmin(final String admin) {
        this.admin = admin;
    }
}
