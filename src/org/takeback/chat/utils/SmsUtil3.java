// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SmsUtil3
{
    static String httpUrl;
    static String testUsername;
    static String testPassword;
    static String sign;
    

	public static void main(final String[] args) {
        send("13625974105", "\u542c\u95fb,\u5b89\u6eaa\u6709\u4e2a\u59d3\u674e\u76842XD!");
    }
    
    public static boolean send(final String phone, final String content) {
        final StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(SmsUtil3.testUsername).append("&");
        httpArg.append("p=").append(md5(SmsUtil3.testPassword)).append("&");
        httpArg.append("m=").append(phone).append("&");
        httpArg.append("c=").append(encodeUrlString(content, "UTF-8")).append(encodeUrlString(SmsUtil3.sign, "UTF-8"));
        final String result = request(SmsUtil3.httpUrl, httpArg.toString());
        return "0".equals(result);
    }
    
    public static String request(String httpUrl, final String httpArg) {
        BufferedReader reader = null;
        String result = null;
        final StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;
        try {
            final URL url = new URL(httpUrl);
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            final InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            reader.close();
            result = sbf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String md5(final String plainText) {
        StringBuffer buf = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            final byte[] b = md.digest();
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; ++offset) {
                int i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
    
    public static String encodeUrlString(final String str, final String charset) {
        String strret = null;
        if (str == null) {
            return str;
        }
        try {
            strret = URLEncoder.encode(str, charset);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }
    
    static {
        SmsUtil3.httpUrl = "http://api.smsbao.com/sms";
        SmsUtil3.testUsername = "test02";
        SmsUtil3.testPassword = "test02";
        SmsUtil3.sign = "\u3010\u75af\u72c2\u7684\u7ea2\u5305\u3011";
    }
}
