// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Random;

public class Sha1Util
{
    public static String getNonceStr() {
        final Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
    }
    
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }
    
    public static String createSHA1Sign(final SortedMap<String, String> signParams) throws Exception {
        final StringBuffer sb = new StringBuffer();
        final  Set<Entry<String, String>> es = signParams.entrySet();
        for (final Map.Entry<String,String> entry : es) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            sb.append(String.valueOf(k) + "=" + v + "&");
        }
        final String params = sb.substring(0, sb.lastIndexOf("&"));
        return getSha1(params);
    }
    
    public static String getSha1(final String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            final MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));
            final byte[] md = mdTemp.digest();
            final int j = md.length;
            final char[] buf = new char[j * 2];
            int k = 0;
            for (final byte byte0 : md) {
                buf[k++] = hexDigits[byte0 >>> 4 & 0xF];
                buf[k++] = hexDigits[byte0 & 0xF];
            }
            return new String(buf);
        }
        catch (Exception e) {
            return null;
        }
    }
}
