package com.wondertek.mam.util.others;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CodeUtil {
	private final static Log log = LogFactory.getLog(CodeUtil.class);
	
	public static String encodeFromUTF8(String utfStr) {
		String encodeStr = "";
		if (utfStr != null && !"".equals(utfStr)) {
			try {
				encodeStr = URLEncoder.encode(utfStr, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("errorMsg:"+e.getMessage());
			}
		}
		return encodeStr;
	}

	public static String decodeToUTF8(String encodeStr) {
		String utfStr = "";
		if (encodeStr != null && !"".equals(encodeStr)) {
			try {
				utfStr = URLDecoder.decode(encodeStr, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("errorMsg:"+e.getMessage());
			}
		}
		return utfStr;
	}

}
