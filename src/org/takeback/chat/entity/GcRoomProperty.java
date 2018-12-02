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
@Table(name = "gc_room_props")
public class GcRoomProperty
{
    private Integer id;
    private String roomId;
    private String configKey;
    private String alias;
    private String configValue;
    private String info;
    
    public GcRoomProperty() {
    }
    
    public GcRoomProperty(final String key, final String alias, final String value, final String info) {
        this.configKey = key;
        this.alias = alias;
        this.configValue = value;
        this.info = info;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 50)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "alias", nullable = false, precision = 0)
    public String getAlias() {
        return this.alias;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    @Basic
    @Column(name = "info", nullable = false, precision = 0)
    public String getInfo() {
        return this.info;
    }
    
    public void setInfo(final String info) {
        this.info = info;
    }
    
    @Basic
    @Column(name = "configKey", nullable = false, precision = 0)
    public String getConfigKey() {
        return this.configKey;
    }
    
    public void setConfigKey(final String key) {
        this.configKey = key;
    }
    
    @Basic
    @Column(name = "roomId", nullable = false, precision = 0)
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    @Basic
    @Column(name = "configValue", nullable = false, precision = 0)
    public String getConfigValue() {
        return this.configValue;
    }
    
    public void setConfigValue(final String value) {
        this.configValue = value;
    }
}
