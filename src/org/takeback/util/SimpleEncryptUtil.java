// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.util.Random;

public class SimpleEncryptUtil
{
    private static final char[] r;
    private static final char b = '3';
    private static final int binLen;
    private static final int s = 6;
    
    public static String toSerialCode(long id) {
        final char[] buf = new char[32];
        int charPos = 32;
        while (id / SimpleEncryptUtil.binLen > 0L) {
            final int ind = (int)(id % SimpleEncryptUtil.binLen);
            buf[--charPos] = SimpleEncryptUtil.r[ind];
            id /= SimpleEncryptUtil.binLen;
        }
        buf[--charPos] = SimpleEncryptUtil.r[(int)(id % SimpleEncryptUtil.binLen)];
        String str = new String(buf, charPos, 32 - charPos);
        if (str.length() < 6) {
            final StringBuilder sb = new StringBuilder();
            sb.append('3');
            final Random rnd = new Random();
            for (int i = 1; i < 6 - str.length(); ++i) {
                sb.append(SimpleEncryptUtil.r[SimpleEncryptUtil.binLen / i - 1]);
            }
            str += sb.toString();
        }
        return str;
    }
    
    public static long codeToId(final String code) {
        final char[] chs = code.toCharArray();
        long res = 0L;
        for (int i = 0; i < chs.length; ++i) {
            int ind = 0;
            for (int j = 0; j < SimpleEncryptUtil.binLen; ++j) {
                if (chs[i] == SimpleEncryptUtil.r[j]) {
                    ind = j;
                    break;
                }
            }
            if (chs[i] == '3') {
                break;
            }
            if (i > 0) {
                res = res * SimpleEncryptUtil.binLen + ind;
            }
            else {
                res = ind;
            }
        }
        return res;
    }
    
    static {
        r = new char[] { '9', '4', '5', '2', '1', '0', '7', '6', '8' };
        binLen = SimpleEncryptUtil.r.length;
    }
}
