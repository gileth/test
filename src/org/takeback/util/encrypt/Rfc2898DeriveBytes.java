// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.encrypt;

import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.util.Random;

public class Rfc2898DeriveBytes
{
    private static final int BLOCK_SIZE = 20;
    private static Random random;
    private Mac hmacsha1;
    private byte[] salt;
    private int iterations;
    private byte[] buffer;
    private int startIndex;
    private int endIndex;
    private int block;
    
    public Rfc2898DeriveBytes(final byte[] password, final byte[] salt, final int iterations) throws NoSuchAlgorithmException, InvalidKeyException {
        this.buffer = new byte[20];
        this.startIndex = 0;
        this.endIndex = 0;
        this.block = 1;
        this.salt = salt;
        this.iterations = iterations;
        (this.hmacsha1 = Mac.getInstance("HmacSHA1")).init(new SecretKeySpec(password, "HmacSHA1"));
    }
    
    public Rfc2898DeriveBytes(final String password, final int saltSize, final int iterations) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        this.buffer = new byte[20];
        this.startIndex = 0;
        this.endIndex = 0;
        this.block = 1;
        this.salt = randomSalt(saltSize);
        this.iterations = iterations;
        (this.hmacsha1 = Mac.getInstance("HmacSHA1")).init(new SecretKeySpec(password.getBytes("UTF-8"), "HmacSHA1"));
        this.buffer = new byte[20];
        this.block = 1;
        final boolean b = true;
        this.endIndex = (b ? 1 : 0);
        this.startIndex = (b ? 1 : 0);
    }
    
    public Rfc2898DeriveBytes(final String password, final int saltSize) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        this(password, saltSize, 1000);
    }
    
    public Rfc2898DeriveBytes(final String password, final byte[] salt, final int iterations) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        this(password.getBytes("UTF8"), salt, iterations);
    }
    
    public byte[] getSalt() {
        return this.salt;
    }
    
    public String getSaltAsString() {
        return Base64.encodeBase64String(this.salt);
    }
    
    public byte[] getBytes(final int cb) {
        final byte[] result = new byte[cb];
        int offset = 0;
        final int size = this.endIndex - this.startIndex;
        if (size > 0) {
            if (cb < size) {
                System.arraycopy(this.buffer, this.startIndex, result, 0, cb);
                this.startIndex += cb;
                return result;
            }
            System.arraycopy(this.buffer, this.startIndex, result, 0, size);
            final boolean b = false;
            this.endIndex = (b ? 1 : 0);
            this.startIndex = (b ? 1 : 0);
            offset += size;
        }
        while (offset < cb) {
            final byte[] block = this.func();
            final int remainder = cb - offset;
            if (remainder <= 20) {
                System.arraycopy(block, 0, result, offset, remainder);
                offset += remainder;
                System.arraycopy(block, remainder, this.buffer, this.startIndex, 20 - remainder);
                this.endIndex += 20 - remainder;
                return result;
            }
            System.arraycopy(block, 0, result, offset, 20);
            offset += 20;
        }
        return result;
    }
    
    public static byte[] randomSalt(final int size) {
        final byte[] salt = new byte[size];
        Rfc2898DeriveBytes.random.nextBytes(salt);
        return salt;
    }
    
    public static String generateSalt(final int size) {
        final byte[] salt = randomSalt(size);
        return Base64.encodeBase64String(salt);
    }
    
    private byte[] func() {
        this.hmacsha1.update(this.salt, 0, this.salt.length);
        byte[] tempHash = this.hmacsha1.doFinal(getBytesFromInt(this.block));
        this.hmacsha1.reset();
        final byte[] finalHash = tempHash;
        for (int i = 2; i <= this.iterations; ++i) {
            tempHash = this.hmacsha1.doFinal(tempHash);
            for (int j = 0; j < 20; ++j) {
                finalHash[j] ^= tempHash[j];
            }
        }
        if (this.block == Integer.MAX_VALUE) {
            this.block = Integer.MIN_VALUE;
        }
        else {
            ++this.block;
        }
        return finalHash;
    }
    
    private static byte[] getBytesFromInt(final int i) {
        return new byte[] { (byte)(i >>> 24), (byte)(i >>> 16), (byte)(i >>> 8), (byte)i };
    }
    
    static {
        Rfc2898DeriveBytes.random = new Random();
    }
}
