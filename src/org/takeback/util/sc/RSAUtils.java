package org.takeback.util.sc;


import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.Cipher;

import com.taobao.api.internal.util.Base64;
  
public class RSAUtils {  
  
    /** *//** 
     * 加密算法RSA 
     */  
    public static final String KEY_ALGORITHM = "RSA";  
      
    /** *//** 
     * 签名算法 
     */  
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";  
  
    /** *//** 
     * 获取公钥的key 
     */  
    private static final String PUBLIC_KEY = "RSAPublicKey";  
      
    /** *//** 
     * 获取私钥的key 
     */  
    private static final String PRIVATE_KEY = "RSAPrivateKey";  
      
    /** *//** 
     * RSA最大加密明文大小 
     */  
    private static final int MAX_ENCRYPT_BLOCK = 117;  
      
    /** *//** 
     * RSA最大解密密文大小 
     */  
    private static final int MAX_DECRYPT_BLOCK = 128;  
  
    /** *//** 
     * <p> 
     * 生成密钥对(公钥和私钥) 
     * </p> 
     *  
     * @return 
     * @throws Exception 
     */  
/*    public static Map<String, Object> genKeyPair() throws Exception {  
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        keyPairGen.initialize(1024);  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        keyMap.put(PUBLIC_KEY, publicKey);  
        keyMap.put(PRIVATE_KEY, privateKey);  
        return keyMap;  
    }  */
      
    /** *//** 
     * <p> 
     * 用私钥对信息生成数字签名 
     * </p> 
     *  
     * @param data 已加密数据 
     * @param privateKey 私钥(BASE64编码) 
     *  
     * @return 
     * @throws Exception 
     */  
    public static String sign(byte[] data, String privateKey) throws Exception {  
        byte[] keyBytes = Base64.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(privateK);  
        signature.update(data);  
        return Base64.encodeToString(signature.sign(),true);  
    }  
  
    /** *//** 
     * <p> 
     * 校验数字签名 
     * </p> 
     *  
     * @param data 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @param sign 数字签名 
     *  
     * @return 
     * @throws Exception 
     *  
     */  
    public static boolean verify(byte[] data, String publicKey, String sign)  
            throws Exception {  
        byte[] keyBytes = Base64.decode(publicKey);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PublicKey publicK = keyFactory.generatePublic(keySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(publicK);  
        signature.update(data);  
        return signature.verify(Base64.decode(sign));  
    }  
  
    /** *//** 
     * <P> 
     * 私钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param privateKey 私钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)  
            throws Exception {  
        byte[] keyBytes = Base64.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, privateK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 公钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)  
            throws Exception {  
        byte[] keyBytes = Base64.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, publicK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 公钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)  
            throws Exception {  
        byte[] keyBytes = Base64.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, publicK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 私钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param privateKey 私钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) { 
    	try{
            byte[] keyBytes = Base64.decode(privateKey);  
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, privateK);  
            int inputLen = data.length;  
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
            int offSet = 0;  
            byte[] cache;  
            int i = 0;  
            // 对数据分段加密  
            while (inputLen - offSet > 0) {  
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
                } else {  
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);  
                }  
                out.write(cache, 0, cache.length);  
                i++;  
                offSet = i * MAX_ENCRYPT_BLOCK;  
            }  
            byte[] encryptedData = out.toByteArray();  
            out.close();  
            return encryptedData;  
    	}catch(Throwable t){
    		return null;
    	}

    }  
  
    /** *//** 
     * <p> 
     * 获取私钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public static String getPrivateKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        return Base64.encodeToString(key.getEncoded(),true);  
    }  
  
    /** *//** 
     * <p> 
     * 获取公钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public static String getPublicKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return Base64.encodeToString(key.getEncoded(),true);  
    }  
	/**
	 * 系统配置的RSA=private key ;
	 */
	public static String sys_private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ2iwDBhZ/t5azoHZS7nOzrJavXMvkIzB1ldy5yKpd/QLmtnkg9/dVYrM09nKiNPwvR4UMYy1wDjLO3qmxmtupfjiApkhTgRiG4frrth2VTCg8ZLGO1KkW4xxtGUEh0yJ/fs+RXIsy88WwioJH5GwbS5m+5g4JG1pde17YF6B7OPAgMBAAECgYBklacAuAa7pfxPqMxdo1pd5owDj8OPRjRYR8tWdfnl2FbmXc4LaY8bjrFM500x/wEtMWCJN4ONo2fV4C9bsUkv//RWsOPrSk0KCnthtFwB3tOswwHnbZzj5nkcdNyazhTvkWEnYVeM43payTuxlYm10ypi6qZ8isLYCisfVISNoQJBAPg87ZlzjXvmY8CVdzzrBWoudxV2gZO8oPrHTJ03SXho2OXrZ4aWz6Tl/RSuJiL4qSE1cOQmpSNTCk5gYmHXNqcCQQCikJQMyd6CCpLdhEYE5C7h4T5FxHFfp8XcQU948rQ4AOIjd1y5vJdBtUkQyLz+lOEnZCnVQ00teiwzMNhUPqDZAkBaXFNg+F8YKtVE1SOCWUqvTBqtBWZFkPRIVSPeVrG30vKhPv+AToVnURC+5mWbdUibfgiKBEc4hJHL1en6E40hAkABzuvmrIQuHCIAFs2UllkzKAdCTWoZb0tTJOLWbJS2nsPlMxmz8zYXhAW5CxKXHl7WylVwytvb6qT9OMjfPwcZAkEAoovfXgADT+wmKFcIQg7VPMNVQve0BBmZX7BLiYMWlyzZ+DfJYj72X4HcPmE+bbiiXwyovIdPmB6rHp82BgQHtQ==";
	
	/**
	 * 系统配置的RSA = public key;
	 */
	public static String sys_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdosAwYWf7eWs6B2Uu5zs6yWr1zL5CMwdZXcuciqXf0C5rZ5IPf3VWKzNPZyojT8L0eFDGMtcA4yzt6psZrbqX44gKZIU4EYhuH667YdlUwoPGSxjtSpFuMcbRlBIdMif37PkVyLMvPFsIqCR+RsG0uZvuYOCRtaXXte2BegezjwIDAQAB";
	
	
  public  static void test() throws Exception {  
        System.err.println("公钥加密——私钥解密");  
       // String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";  
    	String source = "{\"goods_name\":\"金币\",\"rate\":\"10\",\"expire\":\"1480736339\",\"gkey\":\"yx1\",\"skey\":\"1\",\"gname\":\"游戏1\",\"order_id\":\"123456789\",\"payMethod\":\"zfb\",\"amount\":\"10\"}";
        System.out.println("\r加密前文字：\r\n" + source);  
        byte[] data = source.getBytes();  
        byte[] encodedData =  encryptByPublicKey(data, sys_public_key);  
        System.out.println("加密后文字：\r\n" + new String(encodedData));  
        byte[] decodedData =  decryptByPrivateKey(encodedData, sys_private_key);  
        String target = new String(decodedData);  
        System.out.println("解密后文字: \r\n" + target);  
    }
  
  public static void testp() throws Exception{
	   String s = "EU/ZgTvg5gPspQ/kB+0B4GImX2axK0uvXnfDWSWm/gPV7jP/GnFLnqkwQgaO1WVwstFfsXVoI8fJgWQBiwhR673Vf5uwhcx4bPX87epg5oT2pT0oSsTsuFsPMQ9o2mqxx5oR85bsJUgF4X9rrc8KfPM53aeR7mAHUJgGkzpVnapInJ4owSENGqBPuG4qlgvrdvJ+ihHj7tUvjs6AI0uwqbtZ?zQmU0WnDxABkb5QU4IkQM/Em8ZvK3KYBP/TvmDGaHpuMIVgZzrIojSaaZEiIOXfP5SnHMSVGyvqPzOE4tOJfKao/xVx5Ip/O9mNCkp05EVKY0mTxIXn2JZJil3VpAvO+OK5m3tlELrxNXJkE3irELlhMNzXmrSnsbjFW15AKjRjX52oTPR/uAJl3yXS5xWSNOXluh7saEmqdqJyHVKLqMdBh1vZ0QbF0g7UWiW1gMnI=";
       byte[] pub = Base64.decode(s);
       byte[] decodedData =  decryptByPrivateKey(pub, sys_private_key); 
       System.out.println(new String(decodedData));
  }
}  