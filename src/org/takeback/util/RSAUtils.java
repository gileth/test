// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.security.Key;
import javax.crypto.Cipher;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.KeyPairGenerator;
import java.util.HashMap;

public class RSAUtils
{
    public static final String modulus = "9089364886611821498526663098905902697315223569918231283067545167861189175609507663704506417004003608129988167643198236906120307252556043970944568495496069";
    public static final String public_exponent = "65537";
    public static final String private_exponent = "8640007390348301629517914019142484708403596178268704002948851203476639182033562804261690637122300180940543968006248496671507545980055219069315554410355949";
    
    public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(512);
        final KeyPair keyPair = keyPairGen.generateKeyPair();
        final RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        final RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }
    
    public static RSAPublicKey getPublicKey(final String modulus, final String exponent) {
        try {
            final BigInteger b1 = new BigInteger(modulus);
            final BigInteger b2 = new BigInteger(exponent);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey)keyFactory.generatePublic(keySpec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static RSAPrivateKey getPrivateKey(final String modulus, final String exponent) {
        try {
            final BigInteger b1 = new BigInteger(modulus);
            final BigInteger b2 = new BigInteger(exponent);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String encryptByPublicKey(final String data, final RSAPublicKey publicKey) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, publicKey);
        final int key_len = publicKey.getModulus().bitLength() / 8;
        final String[] datas = splitString(data, key_len - 11);
        String mi = "";
        for (final String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }
    
    public static String decryptByPrivateKey(final String data, final RSAPrivateKey privateKey) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, privateKey);
        final int key_len = privateKey.getModulus().bitLength() / 8;
        final byte[] bytes = data.getBytes();
        final byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        String ming = "";
        final byte[][] splitArray;
        final byte[][] arrays = splitArray = splitArray(bcd, key_len);
        for (final byte[] arr : splitArray) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }
    
    public static byte[] ASCII_To_BCD(final byte[] ascii, final int asc_len) {
        final byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; ++i) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte)(((j >= asc_len) ? 0 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }
    
    public static byte asc_to_bcd(final byte asc) {
        byte bcd;
        if (asc >= 48 && asc <= 57) {
            bcd = (byte)(asc - 48);
        }
        else if (asc >= 65 && asc <= 70) {
            bcd = (byte)(asc - 65 + 10);
        }
        else if (asc >= 97 && asc <= 102) {
            bcd = (byte)(asc - 97 + 10);
        }
        else {
            bcd = (byte)(asc - 48);
        }
        return bcd;
    }
    
    public static String bcd2Str(final byte[] bytes) {
        final char[] temp = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; ++i) {
            char val = (char)((bytes[i] & 0xF0) >> 4 & 0xF);
            temp[i * 2] = (char)((val > '\t') ? (val + 'A' - '\n') : (val + '0'));
            val = (char)(bytes[i] & 0xF);
            temp[i * 2 + 1] = (char)((val > '\t') ? (val + 'A' - '\n') : (val + '0'));
        }
        return new String(temp);
    }
    
    public static String[] splitString(final String string, final int len) {
        final int x = string.length() / len;
        final int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        final String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; ++i) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            }
            else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }
    
    public static byte[][] splitArray(final byte[] data, final int len) {
        final int x = data.length / len;
        final int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        final byte[][] arrays = new byte[x + z][];
        for (int i = 0; i < x + z; ++i) {
            final byte[] arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            }
            else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }
}
