// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

public class WeixinOauth2Token
{
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    
    public String getAccess_token() {
        return this.access_token;
    }
    
    public void setAccess_token(final String access_token) {
        this.access_token = access_token;
    }
    
    public int getExpires_in() {
        return this.expires_in;
    }
    
    public void setExpires_in(final int expires_in) {
        this.expires_in = expires_in;
    }
    
    public String getRefresh_token() {
        return this.refresh_token;
    }
    
    public void setRefresh_token(final String refresh_token) {
        this.refresh_token = refresh_token;
    }
    
    public String getOpenid() {
        return this.openid;
    }
    
    public void setOpenid(final String openid) {
        this.openid = openid;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
}
