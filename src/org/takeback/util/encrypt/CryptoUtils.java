// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.encrypt;

import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class CryptoUtils
{
    private static int saltSize;
    private static int iterations;
    private static int subKeySize;
    
    public static String getSalt() {
        return Rfc2898DeriveBytes.generateSalt(CryptoUtils.saltSize);
    }
    
    public static String getHash(final String password, final String salt) {
        Rfc2898DeriveBytes keyGenerator = null;
        try {
            keyGenerator = new Rfc2898DeriveBytes(password + salt, CryptoUtils.saltSize, CryptoUtils.iterations);
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        final byte[] subKey = keyGenerator.getBytes(CryptoUtils.subKeySize);
        final byte[] bSalt = keyGenerator.getSalt();
        final byte[] hashPassword = new byte[1 + CryptoUtils.saltSize + CryptoUtils.subKeySize];
        System.arraycopy(bSalt, 0, hashPassword, 1, CryptoUtils.saltSize);
        System.arraycopy(subKey, 0, hashPassword, CryptoUtils.saltSize + 1, CryptoUtils.subKeySize);
        return Base64.encodeBase64String(hashPassword);
    }
    
    public static boolean verify(final String hashedPassword, final String password, final String salt) {
        final byte[] hashedPasswordBytes = Base64.decodeBase64(hashedPassword);
        if (hashedPasswordBytes.length != 1 + CryptoUtils.saltSize + CryptoUtils.subKeySize || hashedPasswordBytes[0] != 0) {
            return false;
        }
        final byte[] bSalt = new byte[CryptoUtils.saltSize];
        System.arraycopy(hashedPasswordBytes, 1, bSalt, 0, CryptoUtils.saltSize);
        final byte[] storedSubkey = new byte[CryptoUtils.subKeySize];
        System.arraycopy(hashedPasswordBytes, 1 + CryptoUtils.saltSize, storedSubkey, 0, CryptoUtils.subKeySize);
        Rfc2898DeriveBytes deriveBytes = null;
        try {
            deriveBytes = new Rfc2898DeriveBytes(password + salt, bSalt, CryptoUtils.iterations);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final byte[] generatedSubkey = deriveBytes.getBytes(CryptoUtils.subKeySize);
        return byteArraysEqual(storedSubkey, generatedSubkey);
    }
    
    private static boolean byteArraysEqual(final byte[] storedSubkey, final byte[] generatedSubkey) {
        final int size = storedSubkey.length;
        if (size != generatedSubkey.length) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (storedSubkey[i] != generatedSubkey[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(final String[] args) throws NoSuchAlgorithmException {
        final String salt = getSalt();
        final String password = "admin123";
        final String hashPassword = getHash(password, salt);
        System.out.println("hashPassword:" + hashPassword);
        System.out.println("salt:" + salt);
        System.out.println("password:" + password);
        final boolean result = verify(hashPassword, password, salt);
        System.out.println("Verify:" + result);
    }
    
    static {
        CryptoUtils.saltSize = 32;
        CryptoUtils.iterations = 1000;
        CryptoUtils.subKeySize = 32;
    }
}
