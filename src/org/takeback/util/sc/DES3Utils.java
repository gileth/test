package org.takeback.util.sc;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author frank
 *  DES 3 加密
 */
@SuppressWarnings("restriction")
public class DES3Utils {

	private static final Logger logger = LoggerFactory.getLogger(DES3Utils.class);

	private static byte[] keyiv = { 7, 2, 3, 4, 7, 6, 7, 8 };

	private static byte[] key=  null;

	static{
		try {
			key = new BASE64Decoder().decodeBuffer("#WJjZ$V$$Z2hpa&ts'W5,cHF.c3R1dnd4");
		} catch (Throwable t) {
			logger.error("#logger#", t);
		}
	}



    public static void main(String[] args) throws Exception {
//    	String data = "我是美国人"+1;
//      String	result = DES3Utils.fadeInDES(data);
//		System.out.println("加密结果 : " + result);
//		result = DES3Utils.fadeOutDES("oKBYWAnOA3o4o+wX1ZBepQ==");
//		System.out.println("解密结果 : " + result);
//       String rs =  DES3Utils.desEncode("#WJjZ$V$$Z2hpa&ts'W5,cHF.c3R1dnd4", "中国AB");
//       System.out.println(" rs: " + rs);
        String rs = DES3Utils.desDecode("1151924e", "JkK0IiFoYo9QtRqPIk6joA==");
       System.out.println("解密结果"+ rs);

    }

    /**
     * 使用默认密码，使用默认密码加密
     * @param desStr
     * @return
     * @throws Exception
     */
    public static String fadeInDES(String desStr) throws Exception  {
    	 if(StringUtils.isEmpty(desStr)){return null;}
 	    byte[] bOut  =des3EncodeCBC(key, keyiv,desStr.getBytes("UTF-8"));
 		return new BASE64Encoder().encode(bOut);
	}

	/**
	 * 使用默认密码解密
	 * @param undesStr
	 * @return
	 * @throws Exception
	 */
	public static String fadeOutDES(String undesStr) throws Exception {
		if(StringUtils.isEmpty(undesStr)){return null;}
	    byte[] bOut = des3DecodeCBC(key, keyiv, new BASE64Decoder().decodeBuffer(undesStr));
 		return new String(bOut, "UTF-8");
	}


    private static byte[] des3EncodeECB(byte[] key, byte[] data) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }


    private  static byte[] ees3DecodeECB(byte[] key, byte[] data) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;

    }

    /**
     * 根据 key 和 data 加解密
     * @param keystr
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncode(String keystr,String data) throws Exception{
    	if(StringUtils.isEmpty(data) ||  StringUtils.isEmpty(keystr)){return null;}
    	 byte[] key = new BASE64Decoder().decodeBuffer(keystr);
    	 byte[] str5 = des3EncodeCBC(key, keyiv, data.getBytes("UTF-8"));
    	 return new BASE64Encoder().encode(str5);
    }

    /**
     * 根据key 和 data 加解密
     * @param keystr
     * @param data
     * @return
     * @throws Exception
     */
    public static String desDecode(String keystr,String data)throws Exception{
    	 if(StringUtils.isEmpty(data) ||  StringUtils.isEmpty(keystr)){return null;}
    	 byte[] key = new BASE64Decoder().decodeBuffer(keystr);
    	 byte[] str6 = des3DecodeCBC(key, keyiv, new BASE64Decoder().decodeBuffer(data));
    	 return new String(str6, "UTF-8");
    }


    private static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }


    private static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }


}
