// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import java.io.InputStream;
import java.net.URLDecoder;
import java.io.ByteArrayOutputStream;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;

public class SmsUtil
{
    static String url;
    static String un;
    static String pw;
    static String sign;
    static String codeTpl;
    
    
    
    public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		SmsUtil.url = url;
	}

	public static String getUn() {
		return un;
	}

	public static void setUn(String un) {
		SmsUtil.un = un;
	}

	public static String getPw() {
		return pw;
	}

	public static void setPw(String pw) {
		SmsUtil.pw = pw;
	}

	public static String getSign() {
		return sign;
	}

	public static void setSign(String sign) {
		SmsUtil.sign = sign;
	}

	public static String getCodeTpl() {
		return codeTpl;
	}

	public static void setCodeTpl(String codeTpl) {
		SmsUtil.codeTpl = codeTpl;
	}

	public static String sendSmsCode(final String phoneNo, final String code) {
        return send(phoneNo, SmsUtil.codeTpl.replace("${code}", code) + SmsUtil.sign);
    }
    
    public static void main(final String... args) {
        send("13614416609", "SB" + SmsUtil.sign);
    }
    
    public static String send(final String phone, final String msg) {
        final HttpClient client = new HttpClient(new HttpClientParams(), (HttpConnectionManager)new SimpleHttpConnectionManager(true));
        final GetMethod method = new GetMethod();
        try {
            final URI base = new URI(SmsUtil.url, false);
            method.setURI(new URI(base, "send", false));
            method.setQueryString(new NameValuePair[] { new NameValuePair("un", SmsUtil.un), new NameValuePair("pw", SmsUtil.pw), new NameValuePair("phone", phone), new NameValuePair("rd", "0"), new NameValuePair("msg", msg), new NameValuePair("ex", "") });
            final int result = client.executeMethod((HttpMethod)method);
            if (result != 200) {
                throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
            }
            final InputStream in = method.getResponseBodyAsStream();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            System.out.println(URLDecoder.decode(baos.toString(), "UTF-8"));
        }
        catch (Exception e) {
            method.releaseConnection();
        }
        return "";
    }
    
    static {
        SmsUtil.url = "http://sms.253.com/msg/";
        SmsUtil.un = "N3360826";
        SmsUtil.pw = "cQEm9uVayYad6e";
        SmsUtil.sign = "【百城网】";
        SmsUtil.codeTpl = "您的验证码为:${code}";
    }
}
