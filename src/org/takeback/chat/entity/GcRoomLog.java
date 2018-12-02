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
@Table(name = "gc_roomLog")
public class GcRoomLog
{
    private String id;
    private String roomId;
    private Integer uid;
    private Date createdate;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 50)
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "createdate", nullable = true)
    public Date getCreatedate() {
        return this.createdate;
    }
    
    public void setCreatedate(final Date createdate) {
        this.createdate = createdate;
    }
    
    @Basic
    @Column(name = "roomId", nullable = true)
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    @Basic
    @Column(name = "uid", nullable = true)
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
}
