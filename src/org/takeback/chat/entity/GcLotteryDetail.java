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
import java.math.BigDecimal;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_lottery_detail")
public class GcLotteryDetail
{
    private Integer id;
    private String lotteryid;
    private Integer uid;
    private BigDecimal coin;
    private Date createDate;
    private String roomId;
    private String gameType;
    private double deposit;
    private double addback;
    private double inoutNum;
    private String desc1;
    private int masterId;
    
    public GcLotteryDetail() {
        this.uid = 0;
        this.roomId = "";
        this.gameType = "";
        this.deposit = 0.0;
        this.addback = 0.0;
        this.inoutNum = 0.0;
        this.desc1 = "";
        this.masterId = 0;
    }
    
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
    @Column(name = "lotteryid", nullable = false, length = 50)
    public String getLotteryid() {
        return this.lotteryid;
    }
    
    public void setLotteryid(final String lotteryid) {
        this.lotteryid = lotteryid;
    }
    
    @Basic
    @Column(name = "uid", nullable = false)
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    @Basic
    @Column(name = "coin", nullable = false, precision = 0)
    public BigDecimal getCoin() {
        return this.coin;
    }
    
    public void setCoin(final BigDecimal coin) {
        this.coin = coin;
    }
    
    @Basic
    @Column(name = "createdate", nullable = false)
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final Date createdate) {
        this.createDate = createdate;
    }
    
    @Basic
    @Column(name = "addback", nullable = false)
    public double getAddback() {
        return this.addback;
    }
    
    public void setAddback(final double addback) {
        this.addback = addback;
    }
    
    @Basic
    @Column(name = "deposit", nullable = false)
    public double getDeposit() {
        return this.deposit;
    }
    
    public void setDeposit(final double deposit) {
        this.deposit = deposit;
    }
    
    @Basic
    @Column(name = "gameType", nullable = false)
    public String getGameType() {
        return this.gameType;
    }
    
    public void setGameType(final String gameType) {
        this.gameType = gameType;
    }
    
    @Basic
    @Column(name = "inoutNum", nullable = false)
    public double getInoutNum() {
        return this.inoutNum;
    }
    
    public void setInoutNum(final double inout) {
        this.inoutNum = inout;
    }
    
    @Basic
    @Column(name = "masterId", nullable = false)
    public int getMasterId() {
        return this.masterId;
    }
    
    public void setMasterId(final int masterId) {
        this.masterId = masterId;
    }
    
    @Basic
    @Column(name = "roomId", nullable = false)
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    @Basic
    @Column(name = "desc1", nullable = false)
    public String getDesc1() {
        return this.desc1;
    }
    
    public void setDesc1(final String desc1) {
        this.desc1 = desc1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GcLotteryDetail that = (GcLotteryDetail)o;
        Label_0062: {
            if (this.id != null) {
                if (this.id.equals(that.id)) {
                    break Label_0062;
                }
            }
            else if (that.id == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095: {
            if (this.lotteryid != null) {
                if (this.lotteryid.equals(that.lotteryid)) {
                    break Label_0095;
                }
            }
            else if (that.lotteryid == null) {
                break Label_0095;
            }
            return false;
        }
        Label_0128: {
            if (this.uid != null) {
                if (this.uid.equals(that.uid)) {
                    break Label_0128;
                }
            }
            else if (that.uid == null) {
                break Label_0128;
            }
            return false;
        }
        Label_0161: {
            if (this.coin != null) {
                if (this.coin.equals(that.coin)) {
                    break Label_0161;
                }
            }
            else if (that.coin == null) {
                break Label_0161;
            }
            return false;
        }
        if (this.createDate != null) {
            if (this.createDate.equals(that.createDate)) {
                return true;
            }
        }
        else if (that.createDate == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.id != null) ? this.id.hashCode() : 0;
        result = 31 * result + ((this.lotteryid != null) ? this.lotteryid.hashCode() : 0);
        result = 31 * result + ((this.uid != null) ? this.uid.hashCode() : 0);
        result = 31 * result + ((this.coin != null) ? this.coin.hashCode() : 0);
        result = 31 * result + ((this.createDate != null) ? this.createDate.hashCode() : 0);
        return result;
    }
}
