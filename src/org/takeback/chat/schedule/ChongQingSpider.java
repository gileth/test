// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule;

import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Date;
import org.takeback.util.JSONUtils;
import java.util.Map;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import org.takeback.chat.entity.LotteryLog;
import java.util.List;
import org.takeback.chat.service.LotteryService;
import org.takeback.chat.service.LotteryOpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.dao.BaseDAO;

public class ChongQingSpider
{
    @Autowired
    private BaseDAO dao;
    @Autowired
    private LotteryOpenService lotteryOpenService;
    @Autowired
    private LotteryService lotteryService;
    static final int BEAT = 2;
    static final int MAX = 120;
    static final int CACHE_SIZE = 10;
    private String url;
    private Integer currentOpen;
    private Boolean init;
    public static List<LotteryLog> cache;
    
    public ChongQingSpider() {
        this.url = "http://api.caipiaokong.com/lottery/?name=cqssc&format=json&uid=176977&token=838ce074b53cdcdb5123ea8310140a50f2b87c98";
        this.init = true;
    }
    
    public static LotteryLog getLastLog() {
        return ChongQingSpider.cache.get(0);
    }
    
    public static String getLuckyNum(final String sscNum) {
        for (int i = 0; i < ChongQingSpider.cache.size(); ++i) {
            final LotteryLog ll = ChongQingSpider.cache.get(i);
            if (ll.getId().equals(sscNum)) {
                return ll.getLuckyNumber();
            }
        }
        return null;
    }
    
    private boolean checkCache(final String sscNum) {
        for (int i = 0; i < ChongQingSpider.cache.size(); ++i) {
            final LotteryLog l = ChongQingSpider.cache.get(i);
            if (l.getId().equals(sscNum)) {
                return true;
            }
        }
        return false;
    }
    
    private void addCache(final LotteryLog log) {
        if (ChongQingSpider.cache.size() == 10) {
            ChongQingSpider.cache.remove(ChongQingSpider.cache.size() - 1);
        }
        if (ChongQingSpider.cache.size() > 0) {
            final LotteryLog lotteryLog = ChongQingSpider.cache.get(0);
        }
        ChongQingSpider.cache.add(log);
        System.out.println("抓取到开奖号码：" + log);
        Collections.sort(ChongQingSpider.cache);
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
    
    @Transactional
    public void doStuff() {
        final String urlAll = new StringBuffer(this.url).toString();
        final String charset = "UTF-8";
        final String jsonResult = this.get(urlAll, charset);
        final Map<String,Map<String,String>> newData = JSONUtils.parse(jsonResult, Map.class);
        if (newData == null || newData.size() == 0) {
            return;
        }
        for (final String key : newData.keySet()) {
            if (key.length() != 11) {
                continue;
            }
            if (this.checkCache(key) && !this.init) {
                continue;
            }
            final Map<String, String> value = newData.get(key);
            final LotteryLog log = new LotteryLog();
            log.setId(key);
            log.setLuckyNumber(value.get("number"));
            log.setDateline(value.get("dateline"));
            log.setCatchTime(new Date());
            this.addCache(log);
            try {
                final LotteryLog ll = this.dao.get(LotteryLog.class, key);
                if (ll != null) {
                    continue;
                }
                this.dao.save(LotteryLog.class, log);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.init = false;
    }
    
    public static void main(final String... args) {
        final ChongQingSpider s = new ChongQingSpider();
        while (true) {
            try {
                Thread.sleep(2000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            s.doStuff();
        }
    }
    
    private void showCache() {
        System.out.print("cache@[");
        for (int i = 0; i < ChongQingSpider.cache.size(); ++i) {
            System.out.print(ChongQingSpider.cache.get(i) + ",");
        }
        System.out.println("]");
    }
    
    static {
        ChongQingSpider.cache = new ArrayList<LotteryLog>();
    }
}
