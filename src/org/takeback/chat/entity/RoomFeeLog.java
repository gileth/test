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
@Table(name = "gc_roomFeeLog")
public class RoomFeeLog
{
    @Column
    @Id
    @GeneratedValue
    private String id;
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
