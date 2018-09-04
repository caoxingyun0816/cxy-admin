/*
package com.wondertek.mam.util;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DEScrypt {

	public static Cipher cipher = null;


	public static String encrypt(String data,String key) throws Exception {
		if (null == data || 0 == data.length())
			return "";
		SecureRandom sr = new SecureRandom();
		cipher = Cipher.getInstance("DES");
		SecretKeySpec sk = new SecretKeySpec(key.getBytes(),"DES");
		cipher.init(1, sk,sr);
		byte array[];
		byte tmp[] = data.getBytes("UTF-8");
		array = cipher.doFinal(tmp);
		String encrStr=(new BASE64Encoder()).encode(array);
		if(encrStr!=null&&!encrStr.equals("")){
			encrStr=encrStr.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
		}
		return encrStr.replace("/", "|");
	}

	public static String decrypt(String data,String key) throws Exception {
		if (null == data || 0 == data.length())
			return "";
		SecureRandom sr = new SecureRandom();
		cipher = Cipher.getInstance("DES");
		SecretKeySpec sk = new SecretKeySpec(key.getBytes(),"DES");
		cipher.init(2, sk, sr);
		byte array[];
		byte tmp[] = (new BASE64Decoder()).decodeBuffer(data.replace("|", "/"));
		array = cipher.doFinal(tmp);
		return new String(array);
	}

}
*/
