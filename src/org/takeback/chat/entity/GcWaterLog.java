// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_waterLog")
public class GcWaterLog
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private String id;
    @Column
    private Integer uid;
    @Column
    private String userId;
    @Column
    private String roomId;
    @Column
    private String lotteryId;
    @Column
    private String gameType;
    @Column
    private double fullWater;
    @Column
    private double water;
    @Column
    private Integer parentId;
    @Column
    private String parentUserId;
    @Column
    private Date createDate;
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final Date createdate) {
        this.createDate = createdate;
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
    
    public String getLotteryId() {
        return this.lotteryId;
    }
    
    public void setLotteryId(final String lotteryId) {
        this.lotteryId = lotteryId;
    }
    
    public String getGameType() {
        return this.gameType;
    }
    
    public void setGameType(final String gameType) {
        this.gameType = gameType;
    }
    
    public double getFullWater() {
        return this.fullWater;
    }
    
    public void setFullWater(final double fullWater) {
        this.fullWater = fullWater;
    }
    
    public double getWater() {
        return this.water;
    }
    
    public void setWater(final double water) {
        this.water = water;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public Integer getParentId() {
        return this.parentId;
    }
    
    public void setParentId(final Integer parentId) {
        this.parentId = parentId;
    }
    
    public String getParentUserId() {
        return this.parentUserId;
    }
    
    public void setParentUserId(final String parentUserId) {
        this.parentUserId = parentUserId;
    }
}
