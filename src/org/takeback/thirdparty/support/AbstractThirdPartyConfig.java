// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.thirdparty.support;

public abstract class AbstractThirdPartyConfig
{
    private String gameServerBaseUrl;
    
    public String getGameServerBaseUrl() {
        return this.gameServerBaseUrl;
    }
    
    public void setGameServerBaseUrl(final String gameServerBaseUrl) {
        this.gameServerBaseUrl = gameServerBaseUrl;
    }
}
