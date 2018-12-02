// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.security.MessageDigest;

public class MD5Util
{
    private static final String[] hexDigits;
    
    static {
        hexDigits = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    }
    
    private static String byteArrayToHexString(final byte[] b) {
        final StringBuffer resultSb = new StringBuffer();
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
        return String.valueOf(MD5Util.hexDigits[d1]) + MD5Util.hexDigits[d2];
    }
    
    public static String MD5Encode(final String origin, final String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            final MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            }
            else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        }
        catch (Exception ex) {}
        return resultString;
    }
}
