package org.takeback.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * @author xiongguohui
 * 序列号对象工具类
 */
public class SerializeUtil {
	private static Logger logger = Logger.getLogger(SerializeUtil.class);
	public static byte[] serialize(Object object) {
	 ObjectOutputStream oos = null;
     ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("serialize.error",e);
		}finally{
			try {
				if(oos!=null){
					oos.close();
				}
				if(baos!=null){
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("unserialize.error",e);
		}finally{
			try {
				if(ois!=null){
					ois.close();
				}
				if(bais!=null){
					bais.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
