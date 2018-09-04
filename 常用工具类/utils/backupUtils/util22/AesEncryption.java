package com.wondertek.mam.util.backupUtils.util22;

import com.wondertek.mam.util.encryption.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
算法/模式/填充                                                           16字节加密后数据长度                                         不满16字节加密后长度
AES/CBC/NoPadding             16                          不支持
AES/CBC/PKCS5Padding          32                          16
AES/CBC/ISO10126Padding       32                          16
AES/CFB/NoPadding             16                          原始数据长度
AES/CFB/PKCS5Padding          32                          16
AES/CFB/ISO10126Padding       32                          16
AES/ECB/NoPadding             16                          不支持
AES/ECB/PKCS5Padding          32                          16
AES/ECB/ISO10126Padding       32                          16
AES/OFB/NoPadding             16                          原始数据长度
AES/OFB/PKCS5Padding          32                          16
AES/OFB/ISO10126Padding       32                          16
AES/PCBC/NoPadding            16                          不支持
AES/PCBC/PKCS5Padding         32                          16
AES/PCBC/ISO10126Padding      32                          16
 *
 */
public class AesEncryption {
	private final static String IV = "0102030405060708";
	private final static String ALGORITHM = "AES/ECB/PKCS5Padding";

	private AesEncryption() {
	}

	public static String encrypt(String srcData, String key) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			byte[] plaintext = null;
			if(ALGORITHM.equals("AES/CBC/NoPadding") || ALGORITHM.equals("AES/ECB/NoPadding")||
				ALGORITHM.equals("AES/PCBC/NoPadding ")){
				int blockSize = cipher.getBlockSize();
				byte[] dataBytes = srcData.getBytes();
				int plaintextLength = dataBytes.length;
				if (plaintextLength % blockSize != 0) {
					plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
				}
				plaintext = new byte[plaintextLength];
				System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			}else{
				plaintext = srcData.getBytes();
			}
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            if(ALGORITHM.contains("ECB")){
    			cipher.init(Cipher.ENCRYPT_MODE, keyspec);
			}else{
				IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
				cipher.init(Cipher.ENCRYPT_MODE, keyspec,ivspec);
			}
			byte[] encrypted = cipher.doFinal(plaintext);
			return Base64.encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String desEncrypt(String encryData, String key) throws Exception {
		try {
			byte[] encryDataByte = Base64.decode(encryData);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			if(ALGORITHM.contains("ECB")){
				cipher.init(Cipher.DECRYPT_MODE, keyspec);
			}else{
				IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
				cipher.init(Cipher.DECRYPT_MODE, keyspec,ivspec);
			}
			byte[] original = cipher.doFinal(encryDataByte);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}