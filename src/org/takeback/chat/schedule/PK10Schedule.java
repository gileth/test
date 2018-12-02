// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule;

import java.util.concurrent.LinkedBlockingQueue;
import org.takeback.chat.utils.DateUtil;
import java.util.Iterator;
import org.takeback.util.JSONUtils;
import java.util.Map;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.takeback.chat.entity.PK10;
import java.util.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.PK10Service;

public class PK10Schedule
{
    @Autowired
    PK10Service pk10Service;
    static final int CACHE_SIZE = 10;
    private String url;
    public static Queue<PK10> cache;
    
    public PK10Schedule() {
        this.url = "http://api.caipiaokong.com/lottery/?name=bjpks&format=json&uid=539488&token=eee615c9d2e418a4137ce5d16e5f6c80bb9fd7cb";
    }
    
    public String get(final String urlAll, final String charset) {
        BufferedReader reader = null;
        String result = null;
        final StringBuffer sbf = new StringBuffer();
        final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
        try {
            final URL url = new URL(urlAll);
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(30000);
            connection.setConnectTimeout(30000);
            connection.setRequestProperty("User-agent", userAgent);
            connection.connect();
            final InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, charset));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public void doStuff() {
        try {
            final String urlAll = this.url;
            final String charset = "UTF-8";
            final String jsonResult = this.get(urlAll, charset);
            final Map<String,Map<String, String>> newData = JSONUtils.parse(jsonResult, Map.class);
            if (newData == null || newData.size() == 0) {
                return;
            }
            for (final String key : newData.keySet()) {
                if (!this.exists(key)) {
                    this.addNew(key, newData.get(key));
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public void addNew(final String key, final Map<String, String> data) {
        if (PK10Schedule.cache.size() == 10) {
            PK10Schedule.cache.poll();
        }
        final PK10 pk = new PK10();
        pk.setLucky(data.get("number"));
        pk.setNumber(key);
        pk.setOpenTime(DateUtil.toDate(data.get("dateline")));
        PK10Schedule.cache.add(pk);
        try {
            this.pk10Service.save(PK10.class, pk);
        }
        catch (Exception ex) {}
    }
    
    public boolean exists(final String key) {
        for (final PK10 pk : PK10Schedule.cache) {
            if (pk.getNumber().equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    public static void main(final String... args) {
    }
    
    static {
        PK10Schedule.cache = new LinkedBlockingQueue<PK10>();
    }
}
