// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_bank")
public class PubBank
{
    private Integer id;
    private Integer userId;
    private String userIdText;
    private String bankName;
    private String branch;
    private String name;
    private String account;
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
    @Column(name = "userId", nullable = false)
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Integer userId) {
        this.setUserIdText(DictionaryController.instance().get("dic.pubuser").getText(userId.toString()));
        this.userId = userId;
    }
    
    @Basic
    @Column(name = "bankName", nullable = true, length = 100)
    public String getBankName() {
        return this.bankName;
    }
    
    public void setBankName(final String bankName) {
        this.bankName = bankName;
    }
    
    @Basic
    @Column(name = "branch", nullable = true, length = 100)
    public String getBranch() {
        return this.branch;
    }
    
    public void setBranch(final String branch) {
        this.branch = branch;
    }
    
    @Basic
    @Column(name = "name", nullable = true, length = 30)
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Basic
    @Column(name = "account", nullable = true, length = 30)
    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(final String account) {
        this.account = account;
    }
    
    @Basic
    @Column(name = "mobile", nullable = true, length = 20)
    public String getMobile() {
        return this.mobile;
    }
    
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }
    
    @Basic
    @Column(name = "createTime", nullable = true)
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    @Basic
    @Column(name = "userIdText", nullable = true)
    public String getUserIdText() {
        return this.userIdText;
    }
    
    public void setUserIdText(final String userIdText) {
        this.userIdText = userIdText;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PubBank pubBank = (PubBank)o;
        Label_0062: {
            if (this.id != null) {
                if (this.id.equals(pubBank.id)) {
                    break Label_0062;
                }
            }
            else if (pubBank.id == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095: {
            if (this.userId != null) {
                if (this.userId.equals(pubBank.userId)) {
                    break Label_0095;
                }
            }
            else if (pubBank.userId == null) {
                break Label_0095;
            }
            return false;
        }
        Label_0128: {
            if (this.bankName != null) {
                if (this.bankName.equals(pubBank.bankName)) {
                    break Label_0128;
                }
            }
            else if (pubBank.bankName == null) {
                break Label_0128;
            }
            return false;
        }
        Label_0161: {
            if (this.branch != null) {
                if (this.branch.equals(pubBank.branch)) {
                    break Label_0161;
                }
            }
            else if (pubBank.branch == null) {
                break Label_0161;
            }
            return false;
        }
        Label_0194: {
            if (this.name != null) {
                if (this.name.equals(pubBank.name)) {
                    break Label_0194;
                }
            }
            else if (pubBank.name == null) {
                break Label_0194;
            }
            return false;
        }
        Label_0227: {
            if (this.account != null) {
                if (this.account.equals(pubBank.account)) {
                    break Label_0227;
                }
            }
            else if (pubBank.account == null) {
                break Label_0227;
            }
            return false;
        }
        Label_0260: {
            if (this.mobile != null) {
                if (this.mobile.equals(pubBank.mobile)) {
                    break Label_0260;
                }
            }
            else if (pubBank.mobile == null) {
                break Label_0260;
            }
            return false;
        }
        if (this.createTime != null) {
            if (this.createTime.equals(pubBank.createTime)) {
                return true;
            }
        }
        else if (pubBank.createTime == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.id != null) ? this.id.hashCode() : 0;
        result = 31 * result + ((this.userId != null) ? this.userId.hashCode() : 0);
        result = 31 * result + ((this.bankName != null) ? this.bankName.hashCode() : 0);
        result = 31 * result + ((this.branch != null) ? this.branch.hashCode() : 0);
        result = 31 * result + ((this.name != null) ? this.name.hashCode() : 0);
        result = 31 * result + ((this.account != null) ? this.account.hashCode() : 0);
        result = 31 * result + ((this.mobile != null) ? this.mobile.hashCode() : 0);
        result = 31 * result + ((this.createTime != null) ? this.createTime.hashCode() : 0);
        return result;
    }
}
