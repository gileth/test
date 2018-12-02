// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.thirdparty.support;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import net.sf.json.JSONObject;

public class HttpHelper
{
    public static JSONObject getJson(final String url) throws IOException {
        String result = "";
        HttpURLConnection conn = null;
        try {
            final URL realUrl = new URL(url);
            conn = (HttpURLConnection)realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return JSONObject.fromObject((Object)result);
    }
    
    public static JSONObject postForJson(final String url, final String data, final String encoding) throws IOException {
        System.out.println(data);
        String result = "";
        HttpURLConnection conn = null;
        try {
            final URL realUrl = new URL(url);
            conn = (HttpURLConnection)realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(data.getBytes(encoding));
            final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return JSONObject.fromObject((Object)result);
    }
}
