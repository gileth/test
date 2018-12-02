// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.io;

import java.util.concurrent.ConcurrentHashMap;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.security.MessageDigest;
import java.util.Map;

public class Bytes
{
    private static final String C64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private static final char[] BASE16;
    private static final char[] BASE64;
    private static final int MASK4 = 15;
    private static final int MASK6 = 63;
    private static final int MASK8 = 255;
    private static final Map<Integer, byte[]> DECODE_TABLE_MAP;
    private static ThreadLocal<MessageDigest> MD;
    
    public static byte[] copyOf(final byte[] src, final int length) {
        final byte[] dest = new byte[length];
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, length));
        return dest;
    }
    
    public static byte[] short2bytes(final short v) {
        final byte[] ret = { 0, 0 };
        short2bytes(v, ret);
        return ret;
    }
    
    public static void short2bytes(final short v, final byte[] b) {
        short2bytes(v, b, 0);
    }
    
    public static void short2bytes(final short v, final byte[] b, final int off) {
        b[off + 1] = (byte)v;
        b[off + 0] = (byte)(v >>> 8);
    }
    
    public static byte[] int2bytes(final int v) {
        final byte[] ret = { 0, 0, 0, 0 };
        int2bytes(v, ret);
        return ret;
    }
    
    public static void int2bytes(final int v, final byte[] b) {
        int2bytes(v, b, 0);
    }
    
    public static void int2bytes(final int v, final byte[] b, final int off) {
        b[off + 3] = (byte)v;
        b[off + 2] = (byte)(v >>> 8);
        b[off + 1] = (byte)(v >>> 16);
        b[off + 0] = (byte)(v >>> 24);
    }
    
    public static byte[] float2bytes(final float v) {
        final byte[] ret = { 0, 0, 0, 0 };
        float2bytes(v, ret);
        return ret;
    }
    
    public static void float2bytes(final float v, final byte[] b) {
        float2bytes(v, b, 0);
    }
    
    public static void float2bytes(final float v, final byte[] b, final int off) {
        final int i = Float.floatToIntBits(v);
        b[off + 3] = (byte)i;
        b[off + 2] = (byte)(i >>> 8);
        b[off + 1] = (byte)(i >>> 16);
        b[off + 0] = (byte)(i >>> 24);
    }
    
    public static byte[] long2bytes(final long v) {
        final byte[] ret = { 0, 0, 0, 0, 0, 0, 0, 0 };
        long2bytes(v, ret);
        return ret;
    }
    
    public static void long2bytes(final long v, final byte[] b) {
        long2bytes(v, b, 0);
    }
    
    public static void long2bytes(final long v, final byte[] b, final int off) {
        b[off + 7] = (byte)v;
        b[off + 6] = (byte)(v >>> 8);
        b[off + 5] = (byte)(v >>> 16);
        b[off + 4] = (byte)(v >>> 24);
        b[off + 3] = (byte)(v >>> 32);
        b[off + 2] = (byte)(v >>> 40);
        b[off + 1] = (byte)(v >>> 48);
        b[off + 0] = (byte)(v >>> 56);
    }
    
    public static byte[] double2bytes(final double v) {
        final byte[] ret = { 0, 0, 0, 0, 0, 0, 0, 0 };
        double2bytes(v, ret);
        return ret;
    }
    
    public static void double2bytes(final double v, final byte[] b) {
        double2bytes(v, b, 0);
    }
    
    public static void double2bytes(final double v, final byte[] b, final int off) {
        final long j = Double.doubleToLongBits(v);
        b[off + 7] = (byte)j;
        b[off + 6] = (byte)(j >>> 8);
        b[off + 5] = (byte)(j >>> 16);
        b[off + 4] = (byte)(j >>> 24);
        b[off + 3] = (byte)(j >>> 32);
        b[off + 2] = (byte)(j >>> 40);
        b[off + 1] = (byte)(j >>> 48);
        b[off + 0] = (byte)(j >>> 56);
    }
    
    public static short bytes2short(final byte[] b) {
        return bytes2short(b, 0);
    }
    
    public static short bytes2short(final byte[] b, final int off) {
        return (short)(((b[off + 1] & 0xFF) << 0) + (b[off + 0] << 8));
    }
    
    public static int bytes2int(final byte[] b) {
        return bytes2int(b, 0);
    }
    
    public static int bytes2int(final byte[] b, final int off) {
        return ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16) + (b[off + 0] << 24);
    }
    
    public static float bytes2float(final byte[] b) {
        return bytes2float(b, 0);
    }
    
    public static float bytes2float(final byte[] b, final int off) {
        final int i = ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16) + (b[off + 0] << 24);
        return Float.intBitsToFloat(i);
    }
    
    public static long bytes2long(final byte[] b) {
        return bytes2long(b, 0);
    }
    
    public static long bytes2long(final byte[] b, final int off) {
        return ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16) + ((b[off + 4] & 0xFFL) << 24) + ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40) + ((b[off + 1] & 0xFFL) << 48) + (b[off + 0] << 56);
    }
    
    public static double bytes2double(final byte[] b) {
        return bytes2double(b, 0);
    }
    
    public static double bytes2double(final byte[] b, final int off) {
        final long j = ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16) + ((b[off + 4] & 0xFFL) << 24) + ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40) + ((b[off + 1] & 0xFFL) << 48) + (b[off + 0] << 56);
        return Double.longBitsToDouble(j);
    }
    
    public static String bytes2hex(final byte[] bs) {
        return bytes2hex(bs, 0, bs.length);
    }
    
    public static String bytes2hex(final byte[] bs, final int off, final int len) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: length < 0, length is " + len);
        }
        if (off + len > bs.length) {
            throw new IndexOutOfBoundsException("bytes2hex: offset + length > array length.");
        }
        int r = off;
        int w = 0;
        final char[] cs = new char[len * 2];
        for (int i = 0; i < len; ++i) {
            final byte b = bs[r++];
            cs[w++] = Bytes.BASE16[b >> 4 & 0xF];
            cs[w++] = Bytes.BASE16[b & 0xF];
        }
        return new String(cs);
    }
    
    public static byte[] hex2bytes(final String str) {
        return hex2bytes(str, 0, str.length());
    }
    
    public static byte[] hex2bytes(final String str, final int off, final int len) {
        if ((len & 0x1) == 0x1) {
            throw new IllegalArgumentException("hex2bytes: ( len & 1 ) == 1.");
        }
        if (off < 0) {
            throw new IndexOutOfBoundsException("hex2bytes: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("hex2bytes: length < 0, length is " + len);
        }
        if (off + len > str.length()) {
            throw new IndexOutOfBoundsException("hex2bytes: offset + length > array length.");
        }
        final int num = len / 2;
        int r = off;
        int w = 0;
        final byte[] b = new byte[num];
        for (int i = 0; i < num; ++i) {
            b[w++] = (byte)(hex(str.charAt(r++)) << 4 | hex(str.charAt(r++)));
        }
        return b;
    }
    
    public static String bytes2base64(final byte[] b) {
        return bytes2base64(b, 0, b.length, Bytes.BASE64);
    }
    
    public static String bytes2base64(final byte[] b, final int offset, final int length) {
        return bytes2base64(b, offset, length, Bytes.BASE64);
    }
    
    public static String bytes2base64(final byte[] b, final String code) {
        return bytes2base64(b, 0, b.length, code);
    }
    
    public static String bytes2base64(final byte[] b, final int offset, final int length, final String code) {
        if (code.length() < 64) {
            throw new IllegalArgumentException("Base64 code length < 64.");
        }
        return bytes2base64(b, offset, length, code.toCharArray());
    }
    
    public static String bytes2base64(final byte[] b, final char[] code) {
        return bytes2base64(b, 0, b.length, code);
    }
    
    public static String bytes2base64(final byte[] bs, final int off, final int len, final char[] code) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("bytes2base64: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("bytes2base64: length < 0, length is " + len);
        }
        if (off + len > bs.length) {
            throw new IndexOutOfBoundsException("bytes2base64: offset + length > array length.");
        }
        if (code.length < 64) {
            throw new IllegalArgumentException("Base64 code length < 64.");
        }
        final boolean pad = code.length > 64;
        final int num = len / 3;
        final int rem = len % 3;
        int r = off;
        int w = 0;
        final char[] cs = new char[num * 4 + ((rem == 0) ? 0 : (pad ? 4 : (rem + 1)))];
        for (int i = 0; i < num; ++i) {
            final int b1 = bs[r++] & 0xFF;
            final int b2 = bs[r++] & 0xFF;
            final int b3 = bs[r++] & 0xFF;
            cs[w++] = code[b1 >> 2];
            cs[w++] = code[(b1 << 4 & 0x3F) | b2 >> 4];
            cs[w++] = code[(b2 << 2 & 0x3F) | b3 >> 6];
            cs[w++] = code[b3 & 0x3F];
        }
        if (rem == 1) {
            final int b4 = bs[r++] & 0xFF;
            cs[w++] = code[b4 >> 2];
            cs[w++] = code[b4 << 4 & 0x3F];
            if (pad) {
                cs[w++] = code[64];
                cs[w++] = code[64];
            }
        }
        else if (rem == 2) {
            final int b4 = bs[r++] & 0xFF;
            final int b5 = bs[r++] & 0xFF;
            cs[w++] = code[b4 >> 2];
            cs[w++] = code[(b4 << 4 & 0x3F) | b5 >> 4];
            cs[w++] = code[b5 << 2 & 0x3F];
            if (pad) {
                cs[w++] = code[64];
            }
        }
        return new String(cs);
    }
    
    public static byte[] base642bytes(final String str) {
        return base642bytes(str, 0, str.length());
    }
    
    public static byte[] base642bytes(final String str, final int offset, final int length) {
        return base642bytes(str, offset, length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=");
    }
    
    public static byte[] base642bytes(final String str, final String code) {
        return base642bytes(str, 0, str.length(), code);
    }
    
    public static byte[] base642bytes(final String str, final int off, final int len, final String code) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("base642bytes: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("base642bytes: length < 0, length is " + len);
        }
        if (off + len > str.length()) {
            throw new IndexOutOfBoundsException("base642bytes: offset + length > string length.");
        }
        if (code.length() < 64) {
            throw new IllegalArgumentException("Base64 code length < 64.");
        }
        int rem = len % 4;
        if (rem == 1) {
            throw new IllegalArgumentException("base642bytes: base64 string length % 4 == 1.");
        }
        int num = len / 4;
        int size = num * 3;
        if (code.length() > 64) {
            if (rem != 0) {
                throw new IllegalArgumentException("base642bytes: base64 string length error.");
            }
            final char pc = code.charAt(64);
            if (str.charAt(off + len - 2) == pc) {
                size -= 2;
                --num;
                rem = 2;
            }
            else if (str.charAt(off + len - 1) == pc) {
                --size;
                --num;
                rem = 3;
            }
        }
        else if (rem == 2) {
            ++size;
        }
        else if (rem == 3) {
            size += 2;
        }
        int r = off;
        int w = 0;
        final byte[] b = new byte[size];
        final byte[] t = decodeTable(code);
        for (int i = 0; i < num; ++i) {
            final int c1 = t[str.charAt(r++)];
            final int c2 = t[str.charAt(r++)];
            final int c3 = t[str.charAt(r++)];
            final int c4 = t[str.charAt(r++)];
            b[w++] = (byte)(c1 << 2 | c2 >> 4);
            b[w++] = (byte)(c2 << 4 | c3 >> 2);
            b[w++] = (byte)(c3 << 6 | c4);
        }
        if (rem == 2) {
            final int c5 = t[str.charAt(r++)];
            final int c6 = t[str.charAt(r++)];
            b[w++] = (byte)(c5 << 2 | c6 >> 4);
        }
        else if (rem == 3) {
            final int c5 = t[str.charAt(r++)];
            final int c6 = t[str.charAt(r++)];
            final int c7 = t[str.charAt(r++)];
            b[w++] = (byte)(c5 << 2 | c6 >> 4);
            b[w++] = (byte)(c6 << 4 | c7 >> 2);
        }
        return b;
    }
    
    public static byte[] base642bytes(final String str, final char[] code) {
        return base642bytes(str, 0, str.length(), code);
    }
    
    public static byte[] base642bytes(final String str, final int off, final int len, final char[] code) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("base642bytes: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("base642bytes: length < 0, length is " + len);
        }
        if (off + len > str.length()) {
            throw new IndexOutOfBoundsException("base642bytes: offset + length > string length.");
        }
        if (code.length < 64) {
            throw new IllegalArgumentException("Base64 code length < 64.");
        }
        final int rem = len % 4;
        if (rem == 1) {
            throw new IllegalArgumentException("base642bytes: base64 string length % 4 == 1.");
        }
        final int num = len / 4;
        int size = num * 3;
        if (code.length > 64) {
            if (rem != 0) {
                throw new IllegalArgumentException("base642bytes: base64 string length error.");
            }
            final char pc = code[64];
            if (str.charAt(off + len - 2) == pc) {
                size -= 2;
            }
            else if (str.charAt(off + len - 1) == pc) {
                --size;
            }
        }
        else if (rem == 2) {
            ++size;
        }
        else if (rem == 3) {
            size += 2;
        }
        int r = off;
        int w = 0;
        final byte[] b = new byte[size];
        for (int i = 0; i < num; ++i) {
            final int c1 = indexOf(code, str.charAt(r++));
            final int c2 = indexOf(code, str.charAt(r++));
            final int c3 = indexOf(code, str.charAt(r++));
            final int c4 = indexOf(code, str.charAt(r++));
            b[w++] = (byte)(c1 << 2 | c2 >> 4);
            b[w++] = (byte)(c2 << 4 | c3 >> 2);
            b[w++] = (byte)(c3 << 6 | c4);
        }
        if (rem == 2) {
            final int c5 = indexOf(code, str.charAt(r++));
            final int c6 = indexOf(code, str.charAt(r++));
            b[w++] = (byte)(c5 << 2 | c6 >> 4);
        }
        else if (rem == 3) {
            final int c5 = indexOf(code, str.charAt(r++));
            final int c6 = indexOf(code, str.charAt(r++));
            final int c7 = indexOf(code, str.charAt(r++));
            b[w++] = (byte)(c5 << 2 | c6 >> 4);
            b[w++] = (byte)(c6 << 4 | c7 >> 2);
        }
        return b;
    }
    
    public static byte[] zip(final byte[] bytes) throws IOException {
        final UnsafeByteArrayOutputStream bos = new UnsafeByteArrayOutputStream();
        final OutputStream os = new DeflaterOutputStream(bos);
        try {
            os.write(bytes);
        }
        finally {
            os.close();
            bos.close();
        }
        return bos.toByteArray();
    }
    
    public static byte[] unzip(final byte[] bytes) throws IOException {
        final UnsafeByteArrayInputStream bis = new UnsafeByteArrayInputStream(bytes);
        final UnsafeByteArrayOutputStream bos = new UnsafeByteArrayOutputStream();
        final InputStream is = new InflaterInputStream(bis);
        try {
            IOUtils.write(is, bos);
            return bos.toByteArray();
        }
        finally {
            is.close();
            bis.close();
            bos.close();
        }
    }
    
    public static byte[] getMD5(final String str) {
        return getMD5(str.getBytes());
    }
    
    public static byte[] getMD5(final byte[] source) {
        final MessageDigest md = getMessageDigest();
        return md.digest(source);
    }
    
    public static byte[] getMD5(final File file) throws IOException {
        final InputStream is = new FileInputStream(file);
        try {
            return getMD5(is);
        }
        finally {
            is.close();
        }
    }
    
    public static byte[] getMD5(final InputStream is) throws IOException {
        return getMD5(is, 8192);
    }
    
    private static byte hex(final char c) {
        if (c <= '9') {
            return (byte)(c - '0');
        }
        if (c >= 'a' && c <= 'f') {
            return (byte)(c - 'a' + '\n');
        }
        if (c >= 'A' && c <= 'F') {
            return (byte)(c - 'A' + '\n');
        }
        throw new IllegalArgumentException("hex string format error [" + c + "].");
    }
    
    private static int indexOf(final char[] cs, final char c) {
        for (int i = 0, len = cs.length; i < len; ++i) {
            if (cs[i] == c) {
                return i;
            }
        }
        return -1;
    }
    
    private static byte[] decodeTable(final String code) {
        final int hash = code.hashCode();
        byte[] ret = Bytes.DECODE_TABLE_MAP.get(hash);
        if (ret == null) {
            if (code.length() < 64) {
                throw new IllegalArgumentException("Base64 code length < 64.");
            }
            ret = new byte[128];
            for (int i = 0; i < 128; ++i) {
                ret[i] = -1;
            }
            for (int i = 0; i < 64; ++i) {
                ret[code.charAt(i)] = (byte)i;
            }
            Bytes.DECODE_TABLE_MAP.put(hash, ret);
        }
        return ret;
    }
    
    private static byte[] getMD5(final InputStream is, final int bs) throws IOException {
        final MessageDigest md = getMessageDigest();
        final byte[] buf = new byte[bs];
    Label_0008:
        while (is.available() > 0) {
            int total = 0;
            while (true) {
                int read;
                while ((read = is.read(buf, total, bs - total)) > 0) {
                    total += read;
                    if (total >= bs) {
                        md.update(buf);
                        continue Label_0008;
                    }
                }
                continue;
            }
        }
        return md.digest();
    }
    
    private static MessageDigest getMessageDigest() {
        MessageDigest ret = Bytes.MD.get();
        if (ret == null) {
            try {
                ret = MessageDigest.getInstance("MD5");
                Bytes.MD.set(ret);
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }
    
    public static void main(final String[] args) throws UnsupportedEncodingException, IOException {
    }
    
    static {
        BASE16 = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
        DECODE_TABLE_MAP = new ConcurrentHashMap<Integer, byte[]>();
        Bytes.MD = new ThreadLocal<MessageDigest>();
    }
}
