// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.util.Date;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_pk10")
public class PK10
{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    String number;
    @Column
    String lucky;
    @Column
    Date openTime;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getNumber() {
        return this.number;
    }
    
    public void setNumber(final String number) {
        this.number = number;
    }
    
    public String getLucky() {
        return this.lucky;
    }
    
    public void setLucky(final String lucky) {
        this.lucky = lucky;
    }
    
    public Date getOpenTime() {
        return this.openTime;
    }
    
    public void setOpenTime(final Date openTime) {
        this.openTime = openTime;
    }
}
