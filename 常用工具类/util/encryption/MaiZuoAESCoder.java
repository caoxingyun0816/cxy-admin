package com.wondertek.mam.util.encryption;

import com.wondertek.mam.util.encryption.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MaiZuoAESCoder {
	/**
	 * 密钥算法
	 */
	private static final String KEY_ALGORITHM = "AES";
	/**
	 * 工作模式/填充方式
	 */
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 转换密钥
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	private static Key toKey(String key) throws NoSuchAlgorithmException {
		// 返回生成指定算法的秘密密钥的 KeyGenerator 对象
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		// 解决linux系统解密异常处理
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(key.getBytes());
		// 初始化此密钥生成器，使其具有确定的密钥大小
		// AES 要求密钥长度为 128
		kg.init(128, secureRandom);
		// 生成一个密钥
		SecretKey secretKey = kg.generateKey();
		byte[] keyByte = secretKey.getEncoded();
		// Base64 b = new Base64();
		// System.out.println(new String(b.encode(keyByte)));
		System.out.println(new String(Base64.encode(keyByte)));
		// 生成密钥
		return new SecretKeySpec(keyByte, KEY_ALGORITHM);
	}

	/**
	 * AES加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static byte[] aesEncrypt(byte[] data, String key) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		// 实例化
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		// 使用密钥初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * AES解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static byte[] aesDecrypt(byte[] data, String key) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		// 实例化
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		// 使用密钥初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * BASE64+AES加密
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String encrypt(String key, String data)
			throws UnsupportedEncodingException, Exception {
		String result = "";
		if (key != null && !"".equals(key)) {
			byte[] dataBytes = aesEncrypt(data.getBytes("UTF-8"), key);
			// Base64 b = new Base64();
			// result = new String(b.encode(dataBytes));
			result = new String(Base64.encode(dataBytes));
			return result;
		}
		return null;
	}

	/**
	 * BASE64+AES解密
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String decrypt(String key, String data)
			throws UnsupportedEncodingException, Exception {
		if (key != null && !"".equals(key)) {
			// base64处理
//			Base64 b = new Base64();
//			byte[] enData = b.decode(data.getBytes("UTF-8"));
			
			byte[] dataBytes = aesDecrypt(Base64.decode(data), key);
			return new String(dataBytes);
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		String KEY = "zhongchou!$FE(^^";
		StringBuffer sb = new StringBuffer();
		sb.append("{\"account\":\"18588247534\",\"nickName\":\"\u77f3\u5934\",\"mobile\":\"18588247534\",\"co\":\"unicom_gz\",\"datetime\":20150114190930}");
		String endata = encrypt(KEY, sb.toString());
		String dedata = decrypt(KEY, endata);
		System.out.println("加密数据(length=" + endata.length() + "):" + endata);
		System.out.println("解密数据(length=" + dedata.length() + ")：" + dedata);
		String urlencode = URLEncoder.encode(endata, "UTF-8");
		System.out.println("URL编码加密数据:(length=" + urlencode.length() + ")"
				+ urlencode);
	}
}
