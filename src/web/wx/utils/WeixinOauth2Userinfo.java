// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

public class WeixinOauth2Userinfo
{
    private String subscribe;
    private String openid;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private Long subscribe_time;
    private String unionid;
    private String remark;
    private Integer groupid;
    private String[] privilege;
    
    public String[] getPrivilege() {
        return this.privilege;
    }
    
    public void setPrivilege(final String[] privilege) {
        this.privilege = privilege;
    }
    
    public String getSubscribe() {
        return this.subscribe;
    }
    
    public void setSubscribe(final String subscribe) {
        this.subscribe = subscribe;
    }
    
    public String getOpenid() {
        return this.openid;
    }
    
    public void setOpenid(final String openid) {
        this.openid = openid;
    }
    
    public String getNickname() {
        return this.nickname;
    }
    
    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }
    
    public Integer getSex() {
        return this.sex;
    }
    
    public void setSex(final Integer sex) {
        this.sex = sex;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public void setLanguage(final String language) {
        this.language = language;
    }
    
    public String getCity() {
        return this.city;
    }
    
    public void setCity(final String city) {
        this.city = city;
    }
    
    public String getProvince() {
        return this.province;
    }
    
    public void setProvince(final String province) {
        this.province = province;
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(final String country) {
        this.country = country;
    }
    
    public String getHeadimgurl() {
        return this.headimgurl;
    }
    
    public void setHeadimgurl(final String headimgurl) {
        this.headimgurl = headimgurl;
    }
    
    public Long getSubscribe_time() {
        return this.subscribe_time;
    }
    
    public void setSubscribe_time(final Long subscribe_time) {
        this.subscribe_time = subscribe_time;
    }
    
    public String getUnionid() {
        return this.unionid;
    }
    
    public void setUnionid(final String unionid) {
        this.unionid = unionid;
    }
    
    public String getRemark() {
        return this.remark;
    }
    
    public void setRemark(final String remark) {
        this.remark = remark;
    }
    
    public Integer getGroupid() {
        return this.groupid;
    }
    
    public void setGroupid(final Integer groupid) {
        this.groupid = groupid;
    }
}
