// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_room_money")
public class GcRoomMoney
{
    @Id
    @Column
    @GeneratedValue
    private Integer id;
    @Basic
    @Column
    private String roomId;
    @Basic
    @Column
    private Double restMoney;
    @Basic
    @Column
    private Double totalMoney;
    
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
    
    public Double getRestMoney() {
        return this.restMoney;
    }
    
    public void setRestMoney(final Double restMoney) {
        this.restMoney = restMoney;
    }
    
    public Double getTotalMoney() {
        return this.totalMoney;
    }
    
    public void setTotalMoney(final Double totalMoney) {
        this.totalMoney = totalMoney;
    }
}
