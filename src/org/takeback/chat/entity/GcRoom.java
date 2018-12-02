// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_room")
public class GcRoom
{
    private String id;
    private String name;
    private String catalog;
    private String type;
    private Integer owner;
    private Integer limitNum;
    private Integer hot;
    private String roomimg;
    private String description;
    private String detail;
    private String rule;
    private String psw;
    private Integer unDead;
    private Date createdate;
    private String status;
    private Double shareRate;
    private Double sumPool;
    private Double poolAdd;
    private Double feeAdd;
    private Double sumFee;
    private Integer sumPack;
    
    public GcRoom() {
        this.status = "0";
    }
    
    @Id
    @Column(name = "id", nullable = false, length = 50)
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Basic
    @Column(name = "catalog", nullable = false, length = 10)
    public String getCatalog() {
        return this.catalog;
    }
    
    public void setCatalog(final String catalog) {
        this.catalog = catalog;
    }
    
    @Basic
    @Column(name = "type", nullable = false, length = 10)
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    @Basic
    @Column(name = "owner", nullable = false)
    public Integer getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Integer owner) {
        this.owner = owner;
    }
    
    @Basic
    @Column(name = "limitNum", nullable = false)
    public Integer getLimitNum() {
        return this.limitNum;
    }
    
    public void setLimitNum(final Integer limitNum) {
        this.limitNum = limitNum;
    }
    
    @Basic
    @Column(name = "hot", nullable = true)
    public Integer getHot() {
        return this.hot;
    }
    
    public void setHot(final Integer hot) {
        this.hot = hot;
    }
    
    @Basic
    @Column(name = "roomimg", nullable = true, length = 100)
    public String getRoomimg() {
        return this.roomimg;
    }
    
    public void setRoomimg(final String roomimg) {
        this.roomimg = roomimg;
    }
    
    @Basic
    @Column(name = "description", nullable = true, length = 300)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Basic
    @Column(name = "psw", nullable = true, length = 30)
    public String getPsw() {
        return this.psw;
    }
    
    public void setPsw(final String psw) {
        this.psw = psw;
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
    @Column(name = "status", nullable = true, length = 10)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    @Basic
    @Column(name = "feeAdd", nullable = true, length = 10)
    public Double getFeeAdd() {
        return this.feeAdd;
    }
    
    public void setFeeAdd(final Double feeAdd) {
        this.feeAdd = feeAdd;
    }
    
    @Basic
    @Column(name = "poolAdd", nullable = true, length = 10)
    public Double getPoolAdd() {
        return this.poolAdd;
    }
    
    public void setPoolAdd(final Double poolAdd) {
        this.poolAdd = poolAdd;
    }
    
    @Basic
    @Column(name = "sumFee", nullable = true, length = 10)
    public Double getSumFee() {
        return this.sumFee;
    }
    
    public void setSumFee(final Double sumFee) {
        this.sumFee = sumFee;
    }
    
    @Basic
    @Column(name = "sumPool", nullable = true, length = 10)
    public Double getSumPool() {
        return this.sumPool;
    }
    
    public void setSumPool(final Double sumPool) {
        this.sumPool = sumPool;
    }
    
    @Basic
    @Column(name = "shareRate", nullable = true)
    public Double getShareRate() {
        return this.shareRate;
    }
    
    public void setShareRate(final Double shareRate) {
        this.shareRate = shareRate;
    }
    
    @Basic
    @Column(name = "rule", nullable = true)
    public String getRule() {
        return this.rule;
    }
    
    public void setRule(final String rule) {
        this.rule = rule;
    }
    
    @Basic
    @Column(name = "detail", nullable = true)
    public String getDetail() {
        return this.detail;
    }
    
    public void setDetail(final String detail) {
        this.detail = detail;
    }
    
    @Basic
    @Column(name = "sumPack", nullable = true)
    public Integer getSumPack() {
        return this.sumPack;
    }
    
    public void setSumPack(final Integer sumPack) {
        this.sumPack = sumPack;
    }
    
    @Basic
    @Column(name = "unDead", nullable = true)
    public Integer getUnDead() {
        return this.unDead;
    }
    
    public void setUnDead(final Integer unDead) {
        this.unDead = unDead;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GcRoom gcGcRoom = (GcRoom)o;
        Label_0062: {
            if (this.id != null) {
                if (this.id.equals(gcGcRoom.id)) {
                    break Label_0062;
                }
            }
            else if (gcGcRoom.id == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095: {
            if (this.name != null) {
                if (this.name.equals(gcGcRoom.name)) {
                    break Label_0095;
                }
            }
            else if (gcGcRoom.name == null) {
                break Label_0095;
            }
            return false;
        }
        Label_0128: {
            if (this.catalog != null) {
                if (this.catalog.equals(gcGcRoom.catalog)) {
                    break Label_0128;
                }
            }
            else if (gcGcRoom.catalog == null) {
                break Label_0128;
            }
            return false;
        }
        Label_0161: {
            if (this.type != null) {
                if (this.type.equals(gcGcRoom.type)) {
                    break Label_0161;
                }
            }
            else if (gcGcRoom.type == null) {
                break Label_0161;
            }
            return false;
        }
        Label_0194: {
            if (this.owner != null) {
                if (this.owner.equals(gcGcRoom.owner)) {
                    break Label_0194;
                }
            }
            else if (gcGcRoom.owner == null) {
                break Label_0194;
            }
            return false;
        }
        Label_0227: {
            if (this.limitNum != null) {
                if (this.limitNum.equals(gcGcRoom.limitNum)) {
                    break Label_0227;
                }
            }
            else if (gcGcRoom.limitNum == null) {
                break Label_0227;
            }
            return false;
        }
        Label_0260: {
            if (this.roomimg != null) {
                if (this.roomimg.equals(gcGcRoom.roomimg)) {
                    break Label_0260;
                }
            }
            else if (gcGcRoom.roomimg == null) {
                break Label_0260;
            }
            return false;
        }
        Label_0293: {
            if (this.description != null) {
                if (this.description.equals(gcGcRoom.description)) {
                    break Label_0293;
                }
            }
            else if (gcGcRoom.description == null) {
                break Label_0293;
            }
            return false;
        }
        Label_0326: {
            if (this.psw != null) {
                if (this.psw.equals(gcGcRoom.psw)) {
                    break Label_0326;
                }
            }
            else if (gcGcRoom.psw == null) {
                break Label_0326;
            }
            return false;
        }
        Label_0359: {
            if (this.createdate != null) {
                if (this.createdate.equals(gcGcRoom.createdate)) {
                    break Label_0359;
                }
            }
            else if (gcGcRoom.createdate == null) {
                break Label_0359;
            }
            return false;
        }
        if (this.status != null) {
            if (this.status.equals(gcGcRoom.status)) {
                return true;
            }
        }
        else if (gcGcRoom.status == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.id != null) ? this.id.hashCode() : 0;
        result = 31 * result + ((this.name != null) ? this.name.hashCode() : 0);
        result = 31 * result + ((this.catalog != null) ? this.catalog.hashCode() : 0);
        result = 31 * result + ((this.type != null) ? this.type.hashCode() : 0);
        result = 31 * result + ((this.owner != null) ? this.owner.hashCode() : 0);
        result = 31 * result + ((this.limitNum != null) ? this.limitNum.hashCode() : 0);
        result = 31 * result + ((this.roomimg != null) ? this.roomimg.hashCode() : 0);
        result = 31 * result + ((this.description != null) ? this.description.hashCode() : 0);
        result = 31 * result + ((this.psw != null) ? this.psw.hashCode() : 0);
        result = 31 * result + ((this.createdate != null) ? this.createdate.hashCode() : 0);
        result = 31 * result + ((this.status != null) ? this.status.hashCode() : 0);
        return result;
    }
}
