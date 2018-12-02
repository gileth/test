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
@Table(name = "gc_room_kick")
public class GcRoomKickLog
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
    private Integer uid;
    @Basic
    @Column
    private String userId;
    @Basic
    @Column
    private Date kickTime;
    
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
    
    public Date getKickTime() {
        return this.kickTime;
    }
    
    public void setKickTime(final Date kickTime) {
        this.kickTime = kickTime;
    }
}
