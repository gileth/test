// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Transient;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pc_log")
public class PcEggLog implements Comparable<PcEggLog>
{
    private Integer id;
    private String exp;
    private String lucky;
    private String special;
    private Date dataTime;
    private Date openTime;
    private Date beginTime;
    private Date expireTime;
    
    public boolean isClosed(final int closeSeconds) {
        return this.expireTime.getTime() - System.currentTimeMillis() < closeSeconds * 1000;
    }
    
    @Id
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "dataTime")
    public Date getDataTime() {
        return this.dataTime;
    }
    
    public void setDataTime(final Date dataTime) {
        this.dataTime = dataTime;
    }
    
    @Basic
    @Column(name = "exp")
    public String getExp() {
        return this.exp;
    }
    
    public void setExp(final String exp) {
        this.exp = exp;
    }
    
    @Basic
    @Column(name = "lucky")
    public String getLucky() {
        return this.lucky;
    }
    
    public void setLucky(final String lucky) {
        this.lucky = lucky;
    }
    
    @Basic
    @Column(name = "openTime")
    public Date getOpenTime() {
        return this.openTime;
    }
    
    public void setOpenTime(final Date openTime) {
        this.openTime = openTime;
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
    public int compareTo(final PcEggLog o) {
        final Long v = Long.valueOf(o.getId()) - Long.valueOf(this.id);
        return (int)(Object)v;
    }
    
    @Override
    public String toString() {
        return this.id + ":" + this.exp + "=" + this.lucky;
    }
    
    @Transient
    public Date getBeginTime() {
        return this.beginTime;
    }
    
    public void setBeginTime(final Date beginTime) {
        this.beginTime = beginTime;
    }
    
    @Transient
    public Date getExpireTime() {
        return this.expireTime;
    }
    
    public void setExpireTime(final Date expireTime) {
        this.expireTime = expireTime;
    }
}
