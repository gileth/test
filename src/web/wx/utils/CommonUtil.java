// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocketFactory;
import java.net.ConnectException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import net.sf.json.JSONObject;

public class CommonUtil
{
    public static final String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    
    public static JSONObject httpsRequest(final String requestUrl, final String requestMethod, final String outputStr) {
        JSONObject jsonObject = null;
        try {
            final TrustManager[] tm = { new MyX509TrustManager() };
            final SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new SecureRandom());
            final SSLSocketFactory ssf = sslContext.getSocketFactory();
            final URL url = new URL(requestUrl);
            final HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            if (outputStr != null) {
                final OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            final StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        }
        catch (ConnectException ex) {}
        catch (Exception ex2) {}
        return jsonObject;
    }
    
    public static String urlEncodeUTF8(final String source) {
        String result = source;
        try {
            result = URLEncoder.encode(source, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String getFileExt(final String contentType) {
        String fileExt = "";
        if ("image/jpeg".equals(contentType)) {
            fileExt = ".jpg";
        }
        else if ("audio/mpeg".equals(contentType)) {
            fileExt = ".mp3";
        }
        else if ("audio/amr".equals(contentType)) {
            fileExt = ".amr";
        }
        else if ("video/mp4".equals(contentType)) {
            fileExt = ".mp4";
        }
        else if ("video/mpeg4".equals(contentType)) {
            fileExt = ".mp4";
        }
        return fileExt;
    }
}
