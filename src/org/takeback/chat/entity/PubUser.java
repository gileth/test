// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.DecimalFormat;
import java.util.Date;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.DynamicInsert;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_user")
@DynamicInsert(true)
@DynamicUpdate(true)
public class PubUser
{
    private Integer id;
    private String userId;
    private String nickName;
    private String mobile;
    private String pwd;
    private String moneyCode;
    private String headImg;
    private String signture;
    private Double money;
    private Double exp;
    private Integer score;
    private String email;
    private String qq;
    private Integer parent;
    private Double point;
    private Double subPoint;
    private String parentTree;
    private String registIp;
    private Date registDate;
    private String lastLoginIp;
    private Date lastLoginDate;
    private String userType;
    private String wxOpenId;
    private String wxRefreshToken;
    private String wbOpenId;
    private String qqOpenId;
    private String alipay;
    private String wx;
    private String salt;
    private Double chargeAmount;
    private String status;
    private String onlineStatus;
    private String accessToken;
    private Date tokenExpireTime;
    
    public PubUser() {
        this.exp = 0.0;
        this.score = 0;
    }
    
    @Transient
    public String getMoneyText() {
        final DecimalFormat df = new DecimalFormat(".##");
        return df.format(this.money);
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
    @Column(name = "userId", nullable = false, length = 32)
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    @Basic
    @Column(name = "nickName", nullable = true, length = 30)
    public String getNickName() {
        if (this.nickName == null || "".equals(this.nickName)) {
            return this.getUserId();
        }
        return this.nickName;
    }
    
    public void setNickName(final String nickName) {
        this.nickName = nickName;
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
    @Column(name = "pwd", nullable = false, length = 100)
    public String getPwd() {
        return this.pwd;
    }
    
    public void setPwd(final String pwd) {
        this.pwd = pwd;
    }
    
    @Basic
    @Column(name = "moneyCode", nullable = true, length = 100)
    public String getMoneyCode() {
        return this.moneyCode;
    }
    
    public void setMoneyCode(final String moneyCode) {
        this.moneyCode = moneyCode;
    }
    
    @Basic
    @Column(name = "headImg", nullable = true, length = 200)
    public String getHeadImg() {
        return this.headImg;
    }
    
    public void setHeadImg(final String headImg) {
        this.headImg = headImg;
    }
    
    @Basic
    @Column(name = "signture", nullable = true, length = 200)
    public String getSignture() {
        return this.signture;
    }
    
    public void setSignture(final String signture) {
        this.signture = signture;
    }
    
    @Basic
    @Column(name = "point")
    public Double getPoint() {
        return this.point;
    }
    
    public void setPoint(final Double point) {
        this.point = point;
    }
    
    @Basic
    @Column(name = "subPoint")
    public Double getSubPoint() {
        return this.subPoint;
    }
    
    public void setSubPoint(final Double subPoint) {
        this.subPoint = subPoint;
    }
    
    @Basic
    @Column(name = "money", nullable = true, precision = 0)
    public Double getMoney() {
        if (this.money == null) {
            return 0.0;
        }
        return this.money;
    }
    
    public void setMoney(final Double money) {
        this.money = money;
    }
    
    @Basic
    @Column(name = "exp", nullable = true)
    public Double getExp() {
        return this.exp;
    }
    
    public void setExp(final Double exp) {
        this.exp = exp;
    }
    
    @Basic
    @Column(name = "parentTree", nullable = true)
    public String getParentTree() {
        return this.parentTree;
    }
    
    public void setParentTree(final String parentTree) {
        this.parentTree = parentTree;
    }
    
    @Basic
    @Column(name = "score", nullable = true)
    public Integer getScore() {
        return this.score;
    }
    
    public void setScore(final Integer score) {
        this.score = score;
    }
    
    @Basic
    @Column(name = "email", nullable = true, length = 200)
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    @Basic
    @Column(name = "QQ", nullable = true, length = 20)
    public String getQq() {
        return this.qq;
    }
    
    public void setQq(final String qq) {
        this.qq = qq;
    }
    
    @Basic
    @Column(name = "parent", nullable = true)
    public Integer getParent() {
        return this.parent;
    }
    
    public void setParent(final Integer parent) {
        this.parent = parent;
    }
    
    @Basic
    @Column(name = "registIP", nullable = true, length = 20)
    public String getRegistIp() {
        return this.registIp;
    }
    
    public void setRegistIp(final String registIp) {
        this.registIp = registIp;
    }
    
    @Basic
    @Column(name = "registDate", nullable = true)
    public Date getRegistDate() {
        return this.registDate;
    }
    
    public void setRegistDate(final Date registDate) {
        this.registDate = registDate;
    }
    
    @Basic
    @Column(name = "lastLoginIP", nullable = true, length = 20)
    public String getLastLoginIp() {
        return this.lastLoginIp;
    }
    
    public void setLastLoginIp(final String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
    
    @Basic
    @Column(name = "lastLoginDate", nullable = true)
    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }
    
    public void setLastLoginDate(final Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    @Basic
    @Column(name = "userType", nullable = false)
    public String getUserType() {
        return this.userType;
    }
    
    public void setUserType(final String userType) {
        this.userType = userType;
    }
    
    @Basic
    @Column(name = "wxOpenId", nullable = true, length = 50)
    public String getWxOpenId() {
        return this.wxOpenId;
    }
    
    public void setWxOpenId(final String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }
    
    @Basic
    @Column(name = "wxRefreshToken", length = 50)
    public String getWxRefreshToken() {
        return this.wxRefreshToken;
    }
    
    public void setWxRefreshToken(final String wxRefreshToken) {
        this.wxRefreshToken = wxRefreshToken;
    }
    
    @Basic
    @Column(name = "wbOpenId", nullable = true, length = 50)
    public String getWbOpenId() {
        return this.wbOpenId;
    }
    
    public void setWbOpenId(final String wbOpenId) {
        this.wbOpenId = wbOpenId;
    }
    
    @Basic
    @Column(name = "qqOpenId", nullable = true, length = 50)
    public String getQqOpenId() {
        return this.qqOpenId;
    }
    
    public void setQqOpenId(final String qqOpenId) {
        this.qqOpenId = qqOpenId;
    }
    
    @Basic
    @Column(name = "alipay", nullable = true, length = 100)
    public String getAlipay() {
        return this.alipay;
    }
    
    public void setAlipay(final String alipay) {
        this.alipay = alipay;
    }
    
    @Basic
    @Column(name = "salt", nullable = true, length = 100)
    public String getSalt() {
        return this.salt;
    }
    
    public void setSalt(final String salt) {
        this.salt = salt;
    }
    
    @Basic
    @Column(name = "chargeAmount")
    public Double getChargeAmount() {
        if (this.chargeAmount == null) {
            return 0.0;
        }
        return this.chargeAmount;
    }
    
    public void setChargeAmount(final Double chargeAmount) {
        this.chargeAmount = chargeAmount;
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
    @Column(name = "onlineStatus", nullable = true, length = 10)
    public String getOnlineStatus() {
        return this.onlineStatus;
    }
    
    public void setOnlineStatus(final String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }
    
    public Date getTokenExpireTime() {
        return this.tokenExpireTime;
    }
    
    public void setTokenExpireTime(final Date tokenExpireTime) {
        this.tokenExpireTime = tokenExpireTime;
    }
    
    public String getWx() {
        return this.wx;
    }
    
    public void setWx(final String wx) {
        this.wx = wx;
    }
}
