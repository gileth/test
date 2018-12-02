// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.thirdparty.support;

public class DunxingConfig extends AbstractThirdPartyConfig
{
    private String appId;
    private String secretKey;
    private String restApiAddress;
    
    public String getAppId() {
        return this.appId;
    }
    
    public void setAppId(final String appId) {
        this.appId = appId;
    }
    
    public String getSecretKey() {
        return this.secretKey;
    }
    
    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }
    
    public String getRestApiAddress() {
        return this.restApiAddress;
    }
    
    public void setRestApiAddress(final String restApiAddress) {
        this.restApiAddress = restApiAddress;
    }
}
