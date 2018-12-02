// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.security.MessageDigest;

public final class SHA1
{
    private static final char[] HEX_DIGITS;
    
    private static String getFormattedText(final byte[] bytes) {
        final int len = bytes.length;
        final StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; ++j) {
            buf.append(SHA1.HEX_DIGITS[bytes[j] >> 4 & 0xF]);
            buf.append(SHA1.HEX_DIGITS[bytes[j] & 0xF]);
        }
        return buf.toString();
    }
    
    public static String encode(final String str) {
        if (str == null) {
            return null;
        }
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
