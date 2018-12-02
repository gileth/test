// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class TrustAnyTrustManager implements X509TrustManager
{
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
