// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.util.Set;
import java.util.SortedMap;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import java.io.InputStream;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import java.util.HashMap;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.HttpClient;
import web.wx.utils.http.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetWxOrderno
{
    public static DefaultHttpClient httpclient;
    
    static {
        GetWxOrderno.httpclient = new DefaultHttpClient();
        GetWxOrderno.httpclient = (DefaultHttpClient)HttpClientConnectionManager.getSSLInstance((HttpClient)GetWxOrderno.httpclient);
    }
    
    public static String getCodeUrl(final String url, final String xmlParam) {
        System.out.println("xml是:" + xmlParam);
        final DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.allow-circular-redirects", true);
        final HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
        String code_url = "";
        try {
            httpost.setEntity((HttpEntity)new StringEntity(xmlParam, "UTF-8"));
            final HttpResponse response = (HttpResponse)GetWxOrderno.httpclient.execute((HttpUriRequest)httpost);
            final String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            final Map<String, Object> dataMap = new HashMap<String, Object>();
            System.out.println("json是:" + jsonStr);
            if (jsonStr.indexOf("FAIL") != -1) {
                return "";
            }
            final Map<String,String> map = doXMLParse(jsonStr);
            code_url = map.get("code_url");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return code_url;
    }
    
    public static String getPayNo(final String url, final String xmlParam) {
        System.out.println("xml是:" + xmlParam);
        final DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.allow-circular-redirects", true);
        final HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
        String prepay_id = "";
        try {
            httpost.setEntity((HttpEntity)new StringEntity(xmlParam, "UTF-8"));
            final HttpResponse response = (HttpResponse)GetWxOrderno.httpclient.execute((HttpUriRequest)httpost);
            final String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            final Map<String, Object> dataMap = new HashMap<String, Object>();
            System.out.println("json是:" + jsonStr);
            if (jsonStr.indexOf("FAIL") != -1) {
                return prepay_id;
            }
            final Map<String,String> map = doXMLParse(jsonStr);
            final String return_code = map.get("return_code");
            prepay_id = map.get("prepay_id");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return prepay_id;
    }
    
    public static String getMweb_url(final String url, final String xmlParam) {
        System.out.println("xml是:" + xmlParam);
        final DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.allow-circular-redirects", true);
        final HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
        String mweb_url = "";
        try {
            httpost.setEntity((HttpEntity)new StringEntity(xmlParam, "UTF-8"));
            final HttpResponse response = (HttpResponse)GetWxOrderno.httpclient.execute((HttpUriRequest)httpost);
            final String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            final Map<String, Object> dataMap = new HashMap<String, Object>();
            System.out.println("json是:" + jsonStr);
            if (jsonStr.indexOf("FAIL") != -1) {
                return mweb_url;
            }
            final Map<String,String> map = doXMLParse(jsonStr);
            final String return_code = map.get("return_code");
            mweb_url = map.get("mweb_url");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mweb_url;
    }
    
    public static Map doXMLParse(final String strxml) throws Exception {
        if (strxml == null || "".equals(strxml)) {
            return null;
        }
        final Map m = new HashMap();
        final InputStream in = String2Inputstream(strxml);
        final SAXBuilder builder = new SAXBuilder();
        final Document doc = builder.build(in);
        final Element root = doc.getRootElement();
        final List<Element> list = root.getChildren();
        for (final Element e : list) {
            final String k = e.getName();
            String v = "";
            final List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            }
            else {
                v = getChildrenText(children);
            }
            m.put(k, v);
        }
        in.close();
        return m;
    }
    
    public static String getChildrenText(final List<Element> children) {
        final StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            for (final Element e : children) {
                final String name = e.getName();
                final String value = e.getTextNormalize();
                final List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }
        return sb.toString();
    }
    
    public static InputStream String2Inputstream(final String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
    
    public static String getRequestXml(final SortedMap parameters) {
        final StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        final Set<Entry<String,String>> es = parameters.entrySet();
        for (final Map.Entry<String,String> entry : es) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            }
            else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
}
