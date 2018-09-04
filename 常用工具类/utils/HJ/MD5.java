package com.wondertek.mam.util.HJ;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 功能：MD5签名处理核心文件
 * */

public class MD5 {

    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String text, String key, String input_charset) {
        text = text + key;
        //System.out.println(DigestUtils.md5Hex(getContentBytes(text, input_charset)));
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if(mysign.equals(sign)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
    	//0310101|2wsx6yhn$0310103|3edc2wsx
    	String accessid = "0310105";
    	String currentTime="20180505202214814";
    	String cpid="699248";
    	String key="1qazXSW2";
		String md5key= "1cb82625f9b757b4f4b5924df682ae98";
    	System.out.println(sign(accessid+currentTime+cpid,key,"utf-8"));

        //页面跳转URLsign生成举例
        //如果：ticket=v6sh7aQLUHa3Y6uWdX91,redirectURL=/server/doSpAdd.jsp,sericeParams=PrmApplyId=111111&CompanyName=新浪&CompanyNo=88888888&BaseId0001&ProductNameProducId000101&CoopType=1,双方系统月底的密钥=23ddsf3sdscd3sdf3sdcsdf3sckdlsdf，则生成的sign为
        //String dispathSign = sign("v6sh7aQLUHa3Y6uWdX91" + "/server/coopAdd.html" + Base64.encode("PrmApplyId=111111&CompanyName=新浪&CompanyNo=88888888&BaseId0001&ProductNameProducId000101&CoopType=1".getBytes("GBK")) , "23ddsf3sdscd3sdf3sdcsdf3sckdlsdf","GBK");
        //System.out.println("dispathSign==" + dispathSign);
        //System.out.println(Base64.encode("PrmApplyId=111111&CompanyName=新浪&CompanyNo=88888888&BaseId0001&ProductNameProducId000101&CoopType=1".getBytes("GBK")));

    }


}