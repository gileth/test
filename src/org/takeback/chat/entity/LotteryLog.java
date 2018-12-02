// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.util.Date;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "ct_log")
public class LotteryLog implements Comparable<LotteryLog>
{
    private String id;
    private String luckyNumber;
    private String dateline;
    private Date catchTime;
    private String game100;
    private String game300;
    private String groupNum;
    private String special;
    private String result300;
    
    @Basic
    @Column(name = "game100")
    public String getGame100() {
        return this.game100;
    }
    
    public void setGame100(final String game100) {
        this.game100 = game100;
    }
    
    @Basic
    @Column(name = "game300")
    public String getGame300() {
        return this.game300;
    }
    
    public void setGame300(final String game300) {
        this.game300 = game300;
    }
    
    @Basic
    @Column(name = "result300")
    public String getResult300() {
        return this.result300;
    }
    
    public void setResult300(final String result300) {
        this.result300 = result300;
    }
    
    @Basic
    @Column(name = "special")
    public String getSpecial() {
        return this.special;
    }
    
    public void setSpecial(final String special) {
        this.special = special;
    }
    
    @Override
    public String toString() {
        return this.id;
    }
    
    @Override
    public int compareTo(final LotteryLog o) {
        final Long v = Long.valueOf(o.getId()) - Long.valueOf(this.id);
        return (int)(Object)v;
    }
    
    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "luckyNumber", nullable = false, length = 10)
    public String getLuckyNumber() {
        return this.luckyNumber;
    }
    
    public void setLuckyNumber(final String luckyNumber) {
        this.luckyNumber = luckyNumber;
    }
    
    @Basic
    @Column(name = "Dateline", nullable = false, length = 30)
    public String getDateline() {
        return this.dateline;
    }
    
    public void setDateline(final String dateline) {
        this.dateline = dateline;
    }
    
    @Basic
    @Column(name = "catchTime", nullable = false)
    public Date getCatchTime() {
        return this.catchTime;
    }
    
    public void setCatchTime(final Date catchTime) {
        this.catchTime = catchTime;
    }
    
    @Basic
    @Column(name = "groupNum")
    public String getGroupNum() {
        return this.groupNum;
    }
    
    public void setGroupNum(final String groupNum) {
        this.groupNum = groupNum;
    }
}
