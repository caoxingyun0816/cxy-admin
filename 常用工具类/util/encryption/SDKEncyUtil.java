package com.wondertek.mam.util.encryption;

import com.wondertek.mobilevideo.core.util.FileUtil;
import org.springframework.beans.BeansException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.Random;

public class SDKEncyUtil {
	public static final boolean isNeedEncrypt = true;

	private final static String sEncy = "aced0005737200146a6176612e73656375726974792e4b6579526570bdf94fb3889aa5430200044c0009616c676f726974686d7400124c6a6176612f6c616e672f537472696e673b5b0007656e636f6465647400025b424c0006666f726d617471007e00014c00047479706574001b4c6a6176612f73656375726974792f4b657952657024547970653b7870740003444553757200025b42acf317f8060854e0020000787000000008a2891994b6ab8c457400035241577e7200196a6176612e73656375726974792e4b6579526570245479706500000000000000001200007872000e6a6176612e6c616e672e456e756d00000000000000001200007870740006534543524554aced0005737200146a6176612e73656375726974792e4b6579526570bdf94fb3889aa5430200044c0009616c676f726974686d7400124c6a6176612f6c616e672f537472696e673b5b0007656e636f6465647400025b424c0006666f726d617471007e00014c00047479706574001b4c6a6176612f73656375726974792f4b657952657024547970653b7870740003444553757200025b42acf317f8060854e0020000787000000008a2891994b6ab8c457400035241577e7200196a6176612e73656375726974792e4b6579526570245479706500000000000000001200007872000e6a6176612e6c616e672e456e756d00000000000000001200007870740006534543524554";
	private final static String ORDER_ENCRYPT_XML_STRING = "<?xml version='1.0' encoding='UTF-8'?><keys><order_key_yt>%s,20140729180000,20200430235959</order_key_yt><order_key_oy>yn7wO3yuNk5,20131001000000,20200430235959</order_key_oy><order_shadow_key>647f0d4a6b8c27d7e1eddadf7ae86713</order_shadow_key><tt2>12222222222222</tt2></keys>";

	/**
	 * 生成密钥文件，如果存在则覆盖，覆盖后则不能正常解密使用上一个密钥加密的文件
	 * 
	 * @param keyFilePathString
	 * @throws Exception
	 */
	public static SecretKey generateKeyFromDefaultValue() throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				hexStringToBytes(sEncy)));
		return (SecretKey) ois.readObject();
	}

	private static byte[] hexStringToBytes(String src) {
		if (src.length() < 1)
			return null;
		byte[] encrypted = new byte[src.length() / 2];
		for (int i = 0; i < src.length() / 2; i++) {
			int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
			encrypted[i] = (byte) (high * 16 + low);
		}
		return encrypted;
	}

	/**
	 * 加密
	 * 
	 * @param key
	 *            SecretKey
	 * @param input
	 *            要加密的数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(SecretKey key, byte[] input) throws Exception {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(input);
	}

	/**
	 * 解密
	 * 
	 * @param key
	 *            SecretKey
	 * @param input
	 *            要解密的数据
	 * @return
	 * @throws Exception
	 */
	public static byte[] deEncrypt(SecretKey key, byte[] input)
			throws Exception {
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(input);
	}

}