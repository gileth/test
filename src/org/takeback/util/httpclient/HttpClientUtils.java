// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.httpclient;

import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Set;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpPost;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import java.io.IOException;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

public class HttpClientUtils
{
    private static final Logger log;
    private static HttpClientBuilder httpClientBuilder;
    private static CloseableHttpClient httpClient;
    public static String encode;
    
    public static String get(final String url) {
        final HttpGet method = new HttpGet(url);
        try {
            final HttpResponse httpResponse = (HttpResponse)HttpClientUtils.httpClient.execute((HttpUriRequest)method);
            final int code = httpResponse.getStatusLine().getStatusCode();
            if (200 == code) {
                final HttpEntity entity = httpResponse.getEntity();
                return EntityUtils.toString(entity, HttpClientUtils.encode);
            }
            throw new CodedBaseRuntimeException(code, "http response error code " + code);
        }
        catch (IOException e) {
            throw new CodedBaseRuntimeException(500, "execute get method [" + url + "] failed", e);
        }
        finally {
            method.releaseConnection();
        }
    }
    
    public static String post(final String url, final Map<String, String> params) {
        final HttpPost method = new HttpPost(url);
        method.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        if (params != null || params.size() > 0) {
            final List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            final Set<String> set = params.keySet();
            for (final String k : set) {
                nvps.add((NameValuePair)new BasicNameValuePair(k, (String)params.get(k)));
            }
            try {
                method.setEntity((HttpEntity)new UrlEncodedFormEntity((List)nvps, HttpClientUtils.encode));
            }
            catch (UnsupportedEncodingException e) {
                throw new CodedBaseRuntimeException(505, "http parameters encode failed");
            }
        }
        try {
            final HttpResponse httpResponse = (HttpResponse)HttpClientUtils.httpClient.execute((HttpUriRequest)method);
            final int code = httpResponse.getStatusLine().getStatusCode();
            if (200 == code) {
                final HttpEntity entity = httpResponse.getEntity();
                return EntityUtils.toString(entity);
            }
            throw new CodedBaseRuntimeException(code, "http response error code " + code);
        }
        catch (IOException e2) {
            throw new CodedBaseRuntimeException(500, "execute post method [" + url + "] failed");
        }
        finally {
            method.releaseConnection();
        }
    }
    
    public static void release() {
        try {
            HttpClientUtils.httpClient.close();
        }
        catch (IOException e) {
            HttpClientUtils.log.error("httpClient close failed.", (Throwable)e);
        }
    }
    
    static {
        log = LoggerFactory.getLogger((Class)HttpClientUtils.class);
        HttpClientUtils.encode = "UTF-8";
        HttpClientUtils.httpClientBuilder = HttpClientBuilder.create();
        HttpClientUtils.httpClient = HttpClientUtils.httpClientBuilder.setConnectionTimeToLive(20L, TimeUnit.SECONDS).build();
    }
}
