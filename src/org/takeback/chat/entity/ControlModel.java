// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import org.takeback.chat.store.room.Room;
import org.takeback.chat.utils.NumberUtil;
import java.util.Date;

public class ControlModel implements Comparable
{
    static Long IDX;
    Long id;
    Date startDate;
    Date lastModifyDate;
    String roomId;
    String roomName;
    Integer uid;
    String userId;
    String nickName;
    Double targetRate;
    String targetRateText;
    Double currentRate;
    String currentRateText;
    Double inoutNum;
    Double win;
    Double lose;
    Integer playTimes;
    String suggests;
    
    public ControlModel(final String roomId, final String roomName, final Integer uid, final String userId, final String nickName, final Double targetRate) {
        this.targetRate = 0.0;
        this.targetRateText = "";
        this.currentRate = 0.0;
        this.currentRateText = "";
        this.inoutNum = 0.0;
        this.win = 0.0;
        this.lose = 0.0;
        this.playTimes = 0;
        final Long idx = ControlModel.IDX;
        ++ControlModel.IDX;
        this.id = idx;
        this.roomId = roomId;
        this.uid = uid;
        this.roomName = roomName;
        this.userId = userId;
        this.nickName = nickName;
        this.targetRate = targetRate;
        this.targetRateText = NumberUtil.round(targetRate * 100.0) + "%";
        this.startDate = new Date();
        this.lastModifyDate = new Date();
    }
    
    public Date getStartDate(final Room room) {
        return this.startDate;
    }
    
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getLastModifyDate() {
        return this.lastModifyDate;
    }
    
    public void setLastModifyDate(final Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }
    
    public Double getTargetRate() {
        return NumberUtil.round(this.targetRate);
    }
    
    public void setTargetRate(final Double targetRate) {
        this.targetRate = targetRate;
        this.targetRateText = NumberUtil.round(targetRate * 100.0) + "%";
    }
    
    public Double getCurrentRate() {
        return NumberUtil.round(this.currentRate);
    }
    
    public void setCurrentRate(final Double currentRate) {
        this.currentRate = currentRate;
    }
    
    public Double getInoutNum() {
        return NumberUtil.round(this.inoutNum);
    }
    
    public void setInoutNum(final Double inoutNum) {
        this.inoutNum += inoutNum;
        if (inoutNum >= 0.0) {
            this.win += inoutNum;
        }
        else {
            this.lose += inoutNum;
        }
        final Integer playTimes = this.playTimes;
        ++this.playTimes;
        this.lastModifyDate = new Date();
        this.currentRate = this.win / (this.win - this.lose);
        this.currentRateText = NumberUtil.round(this.currentRate * 100.0) + "%";
    }
    
    public Double getWin() {
        return NumberUtil.round(this.win);
    }
    
    public void setWin(final Double win) {
        this.win = win;
    }
    
    public Double getLose() {
        return NumberUtil.round(this.lose);
    }
    
    public void setLose(final Double lose) {
        this.lose = lose;
    }
    
    public Integer getPlayTimes() {
        return this.playTimes;
    }
    
    public void setPlayTimes(final Integer playTimes) {
        this.playTimes = playTimes;
    }
    
    public String getSuggests() {
        return this.suggests;
    }
    
    public void setSuggests(final String suggests) {
        this.suggests = suggests;
    }
    
    public Date getStartDate() {
        return this.startDate;
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
    
    @Override
    public int compareTo(final Object o) {
        final ControlModel cm = (ControlModel)o;
        return (int)(cm.getLastModifyDate().getTime() - this.lastModifyDate.getTime());
    }
    
    public String getRoomName() {
        return this.roomName;
    }
    
    public void setRoomName(final String roomName) {
        this.roomName = roomName;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getTargetRateText() {
        return this.targetRateText;
    }
    
    public void setTargetRateText(final String targetRateText) {
        this.targetRateText = targetRateText;
    }
    
    public String getCurrentRateText() {
        return this.currentRateText;
    }
    
    public void setCurrentRateText(final String currentRateText) {
        this.currentRateText = currentRateText;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public String getNickName() {
        return this.nickName;
    }
    
    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }
    
    static {
        ControlModel.IDX = 1L;
    }
}
