// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pc_config")
public class PcConfig
{
    private Integer id;
    private String param;
    private String val;
    private String alias;
    private String info;
    
    public PcConfig() {
    }
    
    public PcConfig(final String name, final String value) {
        this.param = name;
        this.val = value;
    }
    
    public PcConfig(final String name, final String value, final String description) {
        this.param = name;
        this.val = value;
        this.info = description;
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
    @Column(name = "alias", nullable = false)
    public String getAlias() {
        return this.alias;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    @Basic
    @Column(name = "info", nullable = false)
    public String getInfo() {
        return this.info;
    }
    
    public void setInfo(final String info) {
        this.info = info;
    }
    
    @Basic
    @Column(name = "param", nullable = false)
    public String getParam() {
        return this.param;
    }
    
    public void setParam(final String param) {
        this.param = param;
    }
    
    @Basic
    @Column(name = "val", nullable = false)
    public String getVal() {
        return this.val;
    }
    
    public void setVal(final String val) {
        this.val = val;
    }
}
