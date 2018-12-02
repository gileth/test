// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.thirdparty.support;

public class WxConfig extends AbstractThirdPartyConfig
{
    private String wxJSAPIAppId;
    private String wxJSAPISecret;
    
    public String getWxJSAPIAppId() {
        return this.wxJSAPIAppId;
    }
    
    public void setWxJSAPIAppId(final String wxJSAPIAppId) {
        this.wxJSAPIAppId = wxJSAPIAppId;
    }
    
    public String getWxJSAPISecret() {
        return this.wxJSAPISecret;
    }
    
    public void setWxJSAPISecret(final String wxJSAPISecret) {
        this.wxJSAPISecret = wxJSAPISecret;
    }
}
