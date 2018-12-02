// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils.http;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class MySSLSocketFactory extends SSLSocketFactory
{
    private static MySSLSocketFactory mySSLSocketFactory;
    
    static {
        MySSLSocketFactory.mySSLSocketFactory = new MySSLSocketFactory(createSContext());
        MySSLSocketFactory.mySSLSocketFactory = null;
    }
    
    private static SSLContext createSContext() {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("SSL");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslcontext.init(null, new TrustManager[] { new TrustAnyTrustManager() }, null);
        }
        catch (KeyManagementException e2) {
            e2.printStackTrace();
            return null;
        }
        return sslcontext;
    }
    
    private MySSLSocketFactory(final SSLContext sslContext) {
        super(sslContext);
        this.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    }
    
    public static MySSLSocketFactory getInstance() {
        if (MySSLSocketFactory.mySSLSocketFactory != null) {
            return MySSLSocketFactory.mySSLSocketFactory;
        }
        return MySSLSocketFactory.mySSLSocketFactory = new MySSLSocketFactory(createSContext());
    }
}
