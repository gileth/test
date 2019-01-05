package org.takeback.util.sc;


import org.apache.commons.codec.binary.Base64;


public class Base64DecodeUtil {


	
	/** 
	 * 加密，返回字符串
	 * @param plainText
	 * @return
	 */
    public static String base64Encode(String plainText){  
        byte[] b=plainText.getBytes();  
        Base64 base64=new Base64();  
        b=base64.encode(b);  
        String s=new String(b);  
        return s;  
    }  
    
    /** 
     * 加密，返回字符串
     * @param plainText
     * @return
     */
    public static String base64Encode(byte[] plainText){  
    	Base64 base64=new Base64();  
    	plainText=base64.encode(plainText);  
    	String s=new String(plainText);  
    	return s;  
    }  
    
    
    /** 
     * 加密 ，返回byte数组
     * @param plainText
     * @return
     */
    public static byte[] EncodeToByte(String plainText){  
    	byte[] b=plainText.getBytes();  
    	Base64 base64=new Base64();  
    	b=base64.encode(b);  
    	return b;  
    }  
      
    /** 
     * 解密，返回字符串
     * @param encodeStr
     * @return
     */
    public static String base64Decode(String encodeStr){  
        byte[] b=encodeStr.getBytes();  
        Base64 base64=new Base64();  
        b=base64.decode(b);  
        String s=new String(b);  
        return s;  
    }
    
    /** 
     * 解密，返回byte数组
     * @param encodeStr
     * @return
     */
    public static byte[] decodeToByte(String encodeStr){  
    	byte[] b=encodeStr.getBytes();  
    	Base64 base64=new Base64();  
    	b=base64.decode(b);  
    	return b;  
    }
    
}
