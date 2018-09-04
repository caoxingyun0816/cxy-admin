/**
 * Project Name: aggregation-game-gateway
 * File Name: SHA256Util.java
 * Package Name: com.migu.cmam.gateway.util
 * Date: 2018年1月24日下午2:22:36
 * All rights Reserved, Designed By MiGu. Copyright: Copyright(C) 2018-2020. Company MiGu Co., Ltd.
 *
*/

package com.wondertek.mam.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Name: SHA256Util <br/>
 * Description: sha256 <br/>
 * Date: 2018年1月24日 下午2:22:36 <br/>
 *
 * @author wangxiaowei
 * @version
 * @since JDK 1.8
 * @see
 */
public class SHA256Util {
    private static final Logger LOG = LoggerFactory.getLogger(SHA256Util.class);

    private static final int SHA_0XFF = 0xFF;

    private static final int HEXSTR_LEN = 1;

    /*
     * public static void main(final String[] args) { final String str = "yjpsg"
     * + "sharedSecret" + "20170918190139";
     * System.out.println(SHA256Util.getSHA256StrJava(str)); }
     */
    /**
     * getSHA256StrJava: 利用java原生的摘要实现SHA256加密 <br/>
     *
     * @author migu
     * @param str
     *            加密前的报文
     * @return 加密后的报文
     * @since JDK 1.8
     */
    public static String getSHA256StrJava(final String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }
        return encodeStr;
    }

    /**
     * byte2Hex: 将byte转为16进制. <br/>
     *
     * @author migu
     * @param bytes
     *            bytes
     * @return string
     * @since JDK 1.8
     */
    private static String byte2Hex(final byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & SHA256Util.SHA_0XFF);
            if (temp.length() == SHA256Util.HEXSTR_LEN) {
                // 1得到一位的进行补0操作
                stringBuffer.append('0');
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
