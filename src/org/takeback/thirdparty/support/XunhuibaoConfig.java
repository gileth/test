// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.thirdparty.support;

public class XunhuibaoConfig extends AbstractThirdPartyConfig
{
    private String merchantId;
    private String secretKey;
    private String restApiAddress;
    
    public String getMerchantId() {
        return this.merchantId;
    }
    
    public void setMerchantId(final String merchantId) {
        this.merchantId = merchantId;
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
