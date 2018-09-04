package com.wondertek.mam.util.others;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

public class AliPayUtil {
	/**
	 * 解析远程模拟提交后返回的信息，获得token
	 * 
	 * @param text
	 *            要解析的字符串
	 * @param sec_id
	 *            采用的签名方式
	 * @param private_key
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 解析结果
	 * @throws Exception
	 */
	public static String getRequestToken(String text, String sec_id,
			String private_key, String input_charset) throws Exception {
		String request_token = "";
		// 以“&”字符切割字符串
		String[] strSplitText = text.split("&");
		// 把切割后的字符串数组变成变量与数值组合的字典数组
		Map<String, String> paraText = new HashMap<String, String>();
		for (int i = 0; i < strSplitText.length; i++) {

			// 获得第一个=字符的位置
			int nPos = strSplitText[i].indexOf("=");
			// 获得字符串长度
			int nLen = strSplitText[i].length();
			// 获得变量名
			String strKey = strSplitText[i].substring(0, nPos);
			// 获得数值
			String strValue = strSplitText[i].substring(nPos + 1, nLen);
			// 放入MAP类中
			paraText.put(strKey, strValue);
		}

		if (paraText.get("res_data") != null) {
			String res_data = paraText.get("res_data");
			// 解析加密部分字符串（RSA与MD5区别仅此一句）
			if (sec_id.equals("0001")) {
				res_data = AliPayRSA.decrypt(res_data, private_key,
						input_charset);
			}

			// token从res_data中解析出来（也就是说res_data中已经包含token的内容）
			Document document = DocumentHelper.parseText(res_data);
			request_token = document.selectSingleNode(
					"//direct_trade_create_res/request_token").getText();
		}

		if (paraText.get("res_error") != null) {
			String res_error = paraText.get("res_error");
			Document document = DocumentHelper.parseText(res_error);
			String code = document.selectSingleNode("//err/code").getText();
			String msg = document.selectSingleNode("//err/msg").getText();
			throw new Exception("exception code:" + code
					+ " msg:" + msg);

		}
		return request_token;
	}

	/**
	 * 生成签名结果
	 * 
	 * @param sPara
	 *            要签名的数组
	 * @param sec_id
	 *            采用的签名方式
	 * @param key
	 *            交易安全检验码，由数字和字母组成的32位字符串，如果签名方式设置为“MD5”时，请设置该参数
	 * @param private_key
	 *            商户的私钥，如果签名方式设置为“0001”时，请设置该参数
	 * @param input_charset
	 *            编码格式
	 * @return 签名结果字符串
	 */
	public static String buildRequestMysign(Map<String, String> sPara,
			String sec_id, String key, String private_key, String input_charset) {
		String prestr = createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		if (sec_id.equals("MD5")) {
			mysign = AliPayMD5.sign(prestr, key, input_charset);
		}
		if (sec_id.equals("0001")) {
			mysign = AliPayRSA.sign(prestr, private_key, input_charset);
		}
		return mysign;
	}

	/**
	 * 生成要请求给支付宝的参数数组
	 * 
	 * @param sParaTemp
	 *            请求前的参数数组
	 * @param sec_id
	 *            采用的签名方式
	 * @param key
	 *            交易安全检验码，由数字和字母组成的32位字符串，如果签名方式设置为“MD5”时，请设置该参数
	 * @param private_key
	 *            商户的私钥，如果签名方式设置为“0001”时，请设置该参数
	 * @param input_charset
	 *            编码格式
	 * @return 要请求的参数数组
	 */
	public static Map<String, String> buildRequestPara(
			Map<String, String> sParaTemp, String sec_id, String key,
			String private_key, String input_charset) {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = buildRequestMysign(sPara, sec_id, key, private_key,
				input_charset);

		// 签名结果与签名方式加入请求提交参数组中
		sPara.put("sign", mysign);

		return sPara;
	}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
}
