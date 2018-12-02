// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.user;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Maps;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.socket.WebSocketSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import org.takeback.chat.store.Item;

public class User implements Item
{
    public static final String DEFAULT_HEADIMG = "img/avatar.png";
    public static final String SYSTEM_HEADIMG = "img/system.png";
    private Integer id;
    private String userId;
    private String nickName;
    private String mobile;
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
    private String wbOpenId;
    private String qqOpenId;
    private String alipay;
    private Double chargeAmount;
    private String status;
    private String onlineStatus;
    private String accessToken;
    private Date tokenExpireTime;
    private String roomId;
    private Map<String, Object> properties;
    private WebSocketSession webSocketSession;
    private Boolean handsUp;
    private String url;
    private String invitImg;
    
    public Boolean getHandsUp() {
        return this.handsUp;
    }
    
    public void setHandsUp(final Boolean handsUp) {
        this.handsUp = handsUp;
    }
    
    @JsonIgnore
    public WebSocketSession getWebSocketSession() {
        return this.webSocketSession;
    }
    
    public void setWebSocketSession(final WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }
    
    public User() {
        this.exp = 0.0;
        this.score = 0;
        this.handsUp = Boolean.FALSE;
        this.properties = new ConcurrentHashMap<String, Object>();
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getNickName() {
        return StringUtils.isEmpty((CharSequence)this.nickName) ? this.userId : this.nickName;
    }
    
    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }
    
    public String getMobile() {
        return this.mobile;
    }
    
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }
    
    public String getHeadImg() {
        return StringUtils.isEmpty((CharSequence)this.headImg) ? "img/avatar.png" : this.headImg;
    }
    
    public void setHeadImg(final String headImg) {
        this.headImg = headImg;
    }
    
    public String getSignture() {
        return this.signture;
    }
    
    public void setSignture(final String signture) {
        this.signture = signture;
    }
    
    public Double getMoney() {
        final BigDecimal bd = new BigDecimal(this.money);
        return bd.setScale(2, 4).doubleValue();
    }
    
    public void setMoney(final Double money) {
        this.money = money;
    }
    
    public Double getExp() {
        return this.exp;
    }
    
    public void setExp(final Double exp) {
        this.exp = exp;
    }
    
    public Integer getScore() {
        return this.score;
    }
    
    public void setScore(final Integer score) {
        this.score = score;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public String getQq() {
        return this.qq;
    }
    
    public void setQq(final String qq) {
        this.qq = qq;
    }
    
    public Integer getParent() {
        return this.parent;
    }
    
    public void setParent(final Integer parent) {
        this.parent = parent;
    }
    
    public Double getPoint() {
        return this.point;
    }
    
    public void setPoint(final Double point) {
        this.point = point;
    }
    
    public Double getSubPoint() {
        return this.subPoint;
    }
    
    public void setSubPoint(final Double subPoint) {
        this.subPoint = subPoint;
    }
    
    public String getParentTree() {
        return this.parentTree;
    }
    
    public void setParentTree(final String parentTree) {
        this.parentTree = parentTree;
    }
    
    public String getRegistIp() {
        return this.registIp;
    }
    
    public void setRegistIp(final String registIp) {
        this.registIp = registIp;
    }
    
    public Date getRegistDate() {
        return this.registDate;
    }
    
    public void setRegistDate(final Date registDate) {
        this.registDate = registDate;
    }
    
    public String getLastLoginIp() {
        return this.lastLoginIp;
    }
    
    public void setLastLoginIp(final String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
    
    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }
    
    public void setLastLoginDate(final Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    public String getUserType() {
        return this.userType;
    }
    
    public void setUserType(final String userType) {
        this.userType = userType;
    }
    
    public String getWxOpenId() {
        return this.wxOpenId;
    }
    
    public void setWxOpenId(final String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }
    
    public String getWbOpenId() {
        return this.wbOpenId;
    }
    
    public void setWbOpenId(final String wbOpenId) {
        this.wbOpenId = wbOpenId;
    }
    
    public String getQqOpenId() {
        return this.qqOpenId;
    }
    
    public void setQqOpenId(final String qqOpenId) {
        this.qqOpenId = qqOpenId;
    }
    
    public String getAlipay() {
        return this.alipay;
    }
    
    public void setAlipay(final String alipay) {
        this.alipay = alipay;
    }
    
    public Double getChargeAmount() {
        return this.chargeAmount;
    }
    
    public void setChargeAmount(final Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public String getOnlineStatus() {
        return this.onlineStatus;
    }
    
    public void setOnlineStatus(final String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
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
    
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(final String url) {
        this.url = url;
        this.invitImg = "http://pan.baidu.com/share/qrcode?w=300&h=300&url=" + this.url;
    }
    
    public String getInvitImg() {
        return this.invitImg;
    }
    
    public void setInvitImg(final String invitImg) {
        this.invitImg = invitImg;
    }
}
