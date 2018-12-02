// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.util.Date;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.DynamicInsert;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_withdraw")
@DynamicInsert(true)
@DynamicUpdate(true)
public class PubWithdraw
{
    private Long id;
    private Integer uid;
    private String tradeno;
    private Double fee;
    private Date tradetime;
    private Date finishtime;
    private String payno;
    private String goodsname;
    private String descpt;
    private String status;
    private String bankName;
    private String account;
    private String branch;
    private String ownerName;
    private String mobile;
    private String userIdText;
    
    @Basic
    @Column(name = "userIdText", nullable = false, length = 50)
    public String getUserIdText() {
        return this.userIdText;
    }
    
    public void setUserIdText(final String userIdText) {
        this.userIdText = userIdText;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    @Column(name = "uid", nullable = false)
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
        this.setUserIdText(DictionaryController.instance().get("dic.pubuser").getText(uid.toString()));
    }
    
    @Basic
    @Column(name = "tradeno", length = 50)
    public String getTradeno() {
        return this.tradeno;
    }
    
    public void setTradeno(final String tradeno) {
        this.tradeno = tradeno;
    }
    
    @Basic
    @Column(name = "fee", nullable = false, precision = 0)
    public Double getFee() {
        return this.fee;
    }
    
    public void setFee(final Double fee) {
        this.fee = fee;
    }
    
    @Basic
    @Column(name = "tradetime", nullable = false)
    public Date getTradetime() {
        return this.tradetime;
    }
    
    public void setTradetime(final Date tradetime) {
        this.tradetime = tradetime;
    }
    
    @Basic
    @Column(name = "finishtime", nullable = true)
    public Date getFinishtime() {
        return this.finishtime;
    }
    
    public void setFinishtime(final Date finishtime) {
        this.finishtime = finishtime;
    }
    
    @Basic
    @Column(name = "payno", nullable = true, length = 50)
    public String getPayno() {
        return this.payno;
    }
    
    public void setPayno(final String payno) {
        this.payno = payno;
    }
    
    @Basic
    @Column(name = "goodsname", nullable = true, length = 50)
    public String getGoodsname() {
        return this.goodsname;
    }
    
    public void setGoodsname(final String goodsname) {
        this.goodsname = goodsname;
    }
    
    @Basic
    @Column(name = "account", nullable = true, length = 50)
    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(final String account) {
        this.account = account;
    }
    
    @Basic
    @Column(name = "bankName", nullable = true, length = 50)
    public String getBankName() {
        return this.bankName;
    }
    
    public void setBankName(final String bankName) {
        this.bankName = bankName;
    }
    
    @Basic
    @Column(name = "branch", nullable = true, length = 50)
    public String getBranch() {
        return this.branch;
    }
    
    public void setBranch(final String branch) {
        this.branch = branch;
    }
    
    @Basic
    @Column(name = "mobile", nullable = true, length = 50)
    public String getMobile() {
        return this.mobile;
    }
    
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }
    
    @Basic
    @Column(name = "ownerName", nullable = true, length = 50)
    public String getOwnerName() {
        return this.ownerName;
    }
    
    public void setOwnerName(final String name) {
        this.ownerName = name;
    }
    
    @Basic
    @Column(name = "descpt", nullable = true, length = 200)
    public String getDescpt() {
        return this.descpt;
    }
    
    public void setDescpt(final String descpt) {
        this.descpt = descpt;
    }
    
    @Basic
    @Column(name = "status", nullable = false, length = 1)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    @Transient
    public String getStatusText() {
        if (!StringUtils.isEmpty((CharSequence)this.status)) {
            return DictionaryController.instance().get("dic.chat.withdrawStatus").getText(this.status);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PubWithdraw that = (PubWithdraw)o;
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
            if (this.tradeno != null) {
                if (this.tradeno.equals(that.tradeno)) {
                    break Label_0095;
                }
            }
            else if (that.tradeno == null) {
                break Label_0095;
            }
            return false;
        }
        Label_0128: {
            if (this.fee != null) {
                if (this.fee.equals(that.fee)) {
                    break Label_0128;
                }
            }
            else if (that.fee == null) {
                break Label_0128;
            }
            return false;
        }
        Label_0161: {
            if (this.tradetime != null) {
                if (this.tradetime.equals(that.tradetime)) {
                    break Label_0161;
                }
            }
            else if (that.tradetime == null) {
                break Label_0161;
            }
            return false;
        }
        Label_0194: {
            if (this.finishtime != null) {
                if (this.finishtime.equals(that.finishtime)) {
                    break Label_0194;
                }
            }
            else if (that.finishtime == null) {
                break Label_0194;
            }
            return false;
        }
        Label_0227: {
            if (this.payno != null) {
                if (this.payno.equals(that.payno)) {
                    break Label_0227;
                }
            }
            else if (that.payno == null) {
                break Label_0227;
            }
            return false;
        }
        Label_0260: {
            if (this.goodsname != null) {
                if (this.goodsname.equals(that.goodsname)) {
                    break Label_0260;
                }
            }
            else if (that.goodsname == null) {
                break Label_0260;
            }
            return false;
        }
        Label_0293: {
            if (this.descpt != null) {
                if (this.descpt.equals(that.descpt)) {
                    break Label_0293;
                }
            }
            else if (that.descpt == null) {
                break Label_0293;
            }
            return false;
        }
        if (this.status != null) {
            if (this.status.equals(that.status)) {
                return true;
            }
        }
        else if (that.status == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.id != null) ? this.id.hashCode() : 0;
        result = 31 * result + ((this.tradeno != null) ? this.tradeno.hashCode() : 0);
        result = 31 * result + ((this.fee != null) ? this.fee.hashCode() : 0);
        result = 31 * result + ((this.tradetime != null) ? this.tradetime.hashCode() : 0);
        result = 31 * result + ((this.finishtime != null) ? this.finishtime.hashCode() : 0);
        result = 31 * result + ((this.payno != null) ? this.payno.hashCode() : 0);
        result = 31 * result + ((this.goodsname != null) ? this.goodsname.hashCode() : 0);
        result = 31 * result + ((this.descpt != null) ? this.descpt.hashCode() : 0);
        result = 31 * result + ((this.status != null) ? this.status.hashCode() : 0);
        return result;
    }
}
