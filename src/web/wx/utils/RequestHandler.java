// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.SortedMap;

public class RequestHandler
{
    private String tokenUrl;
    private String gateUrl;
    private String notifyUrl;
    private String appid;
    private String appkey;
    private String partnerkey;
    private String appsecret;
    private String key;
    private SortedMap parameters;
    private String Token;
    private String charset;
    private String debugInfo;
    private String last_errcode;
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    public RequestHandler(final HttpServletRequest request, final HttpServletResponse response) {
        this.last_errcode = "0";
        this.request = request;
        this.response = response;
        this.charset = "UTF-8";
        this.parameters = new TreeMap();
        this.notifyUrl = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml";
    }
    
    public void init(final String app_id, final String app_secret, final String partner_key) {
        this.last_errcode = "0";
        this.Token = "token_";
        this.debugInfo = "";
        this.appid = app_id;
        this.partnerkey = partner_key;
        this.appsecret = app_secret;
        this.key = partner_key;
    }
    
    public void init() {
    }
    
    public String getLasterrCode() {
        return this.last_errcode;
    }
    
    public String getGateUrl() {
        return this.gateUrl;
    }
    
    public String getParameter(final String parameter) {
        final String s = (String)this.parameters.get(parameter);
        return (s == null) ? "" : s;
    }
    
    public void setKey(final String key) {
        this.partnerkey = key;
    }
    
    public void setAppKey(final String key) {
        this.appkey = key;
    }
    
    public String UrlEncode(final String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, this.charset).replace("+", "%20");
    }
    
    public String genPackage(final SortedMap<String, String> packageParams) throws UnsupportedEncodingException {
        final String sign = this.createSign(packageParams);
        final StringBuffer sb = new StringBuffer();
        final Set<Entry<String,String>> es = packageParams.entrySet();
        for (final Map.Entry<String,String> entry : es) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            sb.append(String.valueOf(k) + "=" + this.UrlEncode(v) + "&");
        }
        final String packageValue = sb.append("sign=" + sign).toString();
        return packageValue;
    }
    
    public String createSign(final SortedMap<String, String> packageParams) {
        final StringBuffer sb = new StringBuffer();
        final Set<Entry<String,String>> es = packageParams.entrySet();
        for (final Map.Entry<String,String> entry : es) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            if (v != null && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(String.valueOf(k) + "=" + v + "&");
            }
        }
        sb.append("key=" + this.getKey());
        System.out.println("md5 sb:" + sb + "key=" + this.getKey());
        final String sign = MD5Util.MD5Encode(sb.toString(), this.charset).toUpperCase();
        System.out.println("packge签名:" + sign);
        return sign;
    }
    
    public boolean createMd5Sign(final String signParams) {
        final StringBuffer sb = new StringBuffer();
        final Set<Entry<String,String>> es = this.parameters.entrySet();
        for (final Map.Entry<String,String> entry : es) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            if (!"sign".equals(k) && v != null && !"".equals(v)) {
                sb.append(String.valueOf(k) + "=" + v + "&");
            }
        }
        final String enc = TenpayUtil.getCharacterEncoding(this.request, this.response);
        final String sign = MD5Util.MD5Encode(sb.toString(), enc).toLowerCase();
        final String tenpaySign = this.getParameter("sign").toLowerCase();
        this.setDebugInfo(String.valueOf(sb.toString()) + " => sign:" + sign + " tenpaySign:" + tenpaySign);
        return tenpaySign.equals(sign);
    }
    
    public String parseXML() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        final Set<Entry<String,String>> es = this.parameters.entrySet();
        for (final Map.Entry<String,String> entry : es) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            if (v != null && !"".equals(v) && !"appkey".equals(k)) {
                sb.append("<" + k + ">" + this.getParameter(k) + "</" + k + ">\n");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    protected void setDebugInfo(final String debugInfo) {
        this.debugInfo = debugInfo;
    }
    
    public void setPartnerkey(final String partnerkey) {
        this.partnerkey = partnerkey;
    }
    
    public String getDebugInfo() {
        return this.debugInfo;
    }
    
    public String getKey() {
        return this.partnerkey;
    }
}
