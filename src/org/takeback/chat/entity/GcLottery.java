// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_lottery")
public class GcLottery
{
    private String id;
    private BigDecimal money;
    private Integer number;
    private Integer sender;
    private String roomId;
    private String description;
    private String title;
    private Date createTime;
    private String type;
    private Integer expiredSeconds;
    private String status;
    
    @Id
    @Column(name = "id", nullable = false, length = 50)
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "money", nullable = false, precision = 0)
    public BigDecimal getMoney() {
        return this.money;
    }
    
    public void setMoney(final BigDecimal money) {
        this.money = money;
    }
    
    @Basic
    @Column(name = "number", nullable = false)
    public Integer getNumber() {
        return this.number;
    }
    
    public void setNumber(final Integer number) {
        this.number = number;
    }
    
    @Basic
    @Column(name = "sender", nullable = false)
    public Integer getSender() {
        return this.sender;
    }
    
    public void setSender(final Integer sender) {
        this.sender = sender;
    }
    
    @Basic
    @Column(name = "roomid", nullable = false, length = 50)
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomid) {
        this.roomId = roomid;
    }
    
    @Basic
    @Column(name = "description", nullable = true, length = 200)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Basic
    @Column(name = "createtime", nullable = false)
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createtime) {
        this.createTime = createtime;
    }
    
    @Basic
    @Column(name = "status", nullable = false, length = 2)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    @Basic
    @Column(name = "type")
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    @Basic
    @Column(name = "expiredSeconds")
    public Integer getExpiredSeconds() {
        return this.expiredSeconds;
    }
    
    public void setExpiredSeconds(final Integer expiredSeconds) {
        this.expiredSeconds = expiredSeconds;
    }
    
    @Basic
    @Column(name = "title")
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GcLottery gcLottery = (GcLottery)o;
        Label_0062: {
            if (this.id != null) {
                if (this.id.equals(gcLottery.id)) {
                    break Label_0062;
                }
            }
            else if (gcLottery.id == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095: {
            if (this.money != null) {
                if (this.money.equals(gcLottery.money)) {
                    break Label_0095;
                }
            }
            else if (gcLottery.money == null) {
                break Label_0095;
            }
            return false;
        }
        Label_0128: {
            if (this.number != null) {
                if (this.number.equals(gcLottery.number)) {
                    break Label_0128;
                }
            }
            else if (gcLottery.number == null) {
                break Label_0128;
            }
            return false;
        }
        Label_0161: {
            if (this.sender != null) {
                if (this.sender.equals(gcLottery.sender)) {
                    break Label_0161;
                }
            }
            else if (gcLottery.sender == null) {
                break Label_0161;
            }
            return false;
        }
        Label_0194: {
            if (this.roomId != null) {
                if (this.roomId.equals(gcLottery.roomId)) {
                    break Label_0194;
                }
            }
            else if (gcLottery.roomId == null) {
                break Label_0194;
            }
            return false;
        }
        Label_0227: {
            if (this.description != null) {
                if (this.description.equals(gcLottery.description)) {
                    break Label_0227;
                }
            }
            else if (gcLottery.description == null) {
                break Label_0227;
            }
            return false;
        }
        Label_0260: {
            if (this.createTime != null) {
                if (this.createTime.equals(gcLottery.createTime)) {
                    break Label_0260;
                }
            }
            else if (gcLottery.createTime == null) {
                break Label_0260;
            }
            return false;
        }
        if (this.status != null) {
            if (this.status.equals(gcLottery.status)) {
                return true;
            }
        }
        else if (gcLottery.status == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.id != null) ? this.id.hashCode() : 0;
        result = 31 * result + ((this.money != null) ? this.money.hashCode() : 0);
        result = 31 * result + ((this.number != null) ? this.number.hashCode() : 0);
        result = 31 * result + ((this.sender != null) ? this.sender.hashCode() : 0);
        result = 31 * result + ((this.roomId != null) ? this.roomId.hashCode() : 0);
        result = 31 * result + ((this.description != null) ? this.description.hashCode() : 0);
        result = 31 * result + ((this.createTime != null) ? this.createTime.hashCode() : 0);
        result = 31 * result + ((this.status != null) ? this.status.hashCode() : 0);
        return result;
    }
}
