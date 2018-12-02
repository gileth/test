// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.thirdparty.support;

public class FastPayConfig extends AbstractThirdPartyConfig
{
    private String partnerId;
    private String secretCode;
    private String restApiAddress;
    
    public String getPartnerId() {
        return this.partnerId;
    }
    
    public void setPartnerId(final String partnerId) {
        this.partnerId = partnerId;
    }
    
    public String getSecretCode() {
        return this.secretCode;
    }
    
    public void setSecretCode(final String secretCode) {
        this.secretCode = secretCode;
    }
    
    public String getRestApiAddress() {
        return this.restApiAddress;
    }
    
    public void setRestApiAddress(final String restApiAddress) {
        this.restApiAddress = restApiAddress;
    }
}
