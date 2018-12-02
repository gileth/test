// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TenpayUtil
{
    private static Object Server;
    private static String QRfromGoogle;
    
    public static String toString(final Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }
    
    public static int toInt(final Object obj) {
        int a = 0;
        try {
            if (obj != null) {
                a = Integer.parseInt(obj.toString());
            }
        }
        catch (Exception ex) {}
        return a;
    }
    
    public static String getCurrTime() {
        final Date now = new Date();
        final SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        final String s = outFormat.format(now);
        return s;
    }
    
    public static String formatDate(final Date date) {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        final String strDate = formatter.format(date);
        return strDate;
    }
    
    public static int buildRandom(final int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random += 0.1;
        }
        for (int i = 0; i < length; ++i) {
            num *= 10;
        }
        return (int)(random * num);
    }
    
    public static String getCharacterEncoding(final HttpServletRequest request, final HttpServletResponse response) {
        if (request == null || response == null) {
            return "utf-8";
        }
        String enc = request.getCharacterEncoding();
        if (enc == null || "".equals(enc)) {
            enc = response.getCharacterEncoding();
        }
        if (enc == null || "".equals(enc)) {
            enc = "utf-8";
        }
        return enc;
    }
    
    public static String URLencode(final String content) {
        final String URLencode = replace(TenpayUtil.Server.equals(content), "+", "%20");
        return URLencode;
    }
    
    private static String replace(final boolean equals, final String string, final String string2) {
        return null;
    }
    
    public static long getUnixTime(final Date date) {
        if (date == null) {
            return 0L;
        }
        return date.getTime() / 1000L;
    }
    
    public static String QRfromGoogle(String chl) {
        final int widhtHeight = 300;
        final String EC_level = "L";
        final int margin = 0;
        chl = URLencode(chl);
        final String QRfromGoogle = "http://chart.apis.google.com/chart?chs=" + widhtHeight + "x" + widhtHeight + "&cht=qr&chld=" + EC_level + "|" + margin + "&chl=" + chl;
        return QRfromGoogle;
    }
    
    public static String date2String(final Date date, final String formatType) {
        final SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(date);
    }
}
