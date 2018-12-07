// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.security.MessageDigest;

public class MD5StringUtil
{
    private static final String[] hexDigits;
    
    public static String MD5Encode(final byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(data));
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static String MD5Encode(final String origin) {
        String resultString = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes()));
        }
        catch (Exception ex) {}
        return resultString;
    }
    
    public static String MD5EncodeUTF8(final String origin) {
        String resultString = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes("UTF-8")));
        }
        catch (Exception ex) {}
        return resultString;
    }
    
    private static String byteArrayToHexString(final byte[] b) {
        final StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }
    
    private static String byteToHexString(final byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        final int d1 = n / 16;
        final int d2 = n % 16;
        return MD5StringUtil.hexDigits[d1] + MD5StringUtil.hexDigits[d2];
    }
    
    public static void main(final String[] args) {
        System.err.println(MD5Encode("123123张三"));
        System.err.println(MD5EncodeUTF8("123123张三"));
    }
    
    static {
        hexDigits = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    }
}