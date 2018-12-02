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
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_Apply")
public class PubRoomApply
{
    private Integer id;
    private Integer uid;
    private String userIdText;
    private String name;
    private String mobile;
    private Date createTime;
    
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
    @Column(name = "createTime", nullable = false)
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    @Basic
    @Column(name = "mobile", nullable = false)
    public String getMobile() {
        return this.mobile;
    }
    
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }
    
    @Basic
    @Column(name = "name", nullable = false)
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
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
    @Column(name = "userIdText", nullable = false)
    public String getUserIdText() {
        return this.userIdText;
    }
    
    public void setUserIdText(final String userIdText) {
        this.userIdText = userIdText;
    }
}
