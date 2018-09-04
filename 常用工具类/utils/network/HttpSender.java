package com.wondertek.mam.util.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 * 发送http请求的工具类, 配置连接池能够重用已有连接进行循环发送
 * 
 * @author Jimmy Deng
 */
public class HttpSender {

	public static Logger log = Logger.getLogger(HttpSender.class);
	private static MultiThreadedHttpConnectionManager connectionManager;
	/**
	 * maximum number of connections allowed per host
	 */
	private static int maxHostConnections = 100;

	/**
	 * maximum number of connections allowed overall
	 */
	private static int maxTotalConnections = 500;

	/**
	 * the timeout until a connection is etablished
	 */
	private static int CONNECT_TIMEOUT = 3000;

	/**
	 * 获取连接的最大等待时间
	 */
	public final static int WAIT_TIMEOUT = 3000;

	/**
	 * 读取超时时间
	 */
	public final static int READ_TIMEOUT = 3000;

	static {
		connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setDefaultMaxConnectionsPerHost(maxHostConnections);
		params.setMaxTotalConnections(maxTotalConnections);
		params.setConnectionTimeout(CONNECT_TIMEOUT);
		params.setSoTimeout(READ_TIMEOUT);
		connectionManager.setParams(params);
	}

	public static HttpClient getHttpClient() {
		HttpClient client = new HttpClient(connectionManager);
		client.getParams().setParameter(HttpClientParams.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		return client;
	}

	/**
	 * 使用get方式调用
	 * 
	 * @param url调用的URL
	 * @return 调用得到的字符串
	 */
	public static String httpGet(String url) {
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		return getResponseStr(getMethod);
	}

	/**
	 * 使用post方式调用
	 * 
	 * @param url
	 *            调用的URL
	 * @param values
	 *            传递的参数值
	 * @return 调用得到的字符串
	 */
	public static String httpPost(String url, NameValuePair[] values) {
		List<NameValuePair[]> list = new ArrayList<NameValuePair[]>();
		list.add(values);
		return httpPost(url, list);
	}

	/**
	 * 使用post方式调用
	 * 
	 * @param url
	 *            调用的URL
	 * @param params
	 *            参数Map
	 * @return 调用得到的字符串
	 */
	public static String httpPost(String url, Map<String, String> params) {
		PostMethod postMethod = new PostMethod(url);
		for (Entry<String, String> e : params.entrySet()) {
			postMethod.addParameter(new NameValuePair(e.getKey(), e.getValue()));
		}
		return getResponseStr(postMethod);
	}

	/**
	 * 使用post方式调用
	 * 
	 * @param url
	 *            调用的URL
	 * @param values
	 *            传递的参数值List
	 * @return 调用得到的字符串
	 */
	public static String httpPost(String url, List<NameValuePair[]> values) {
		PostMethod postMethod = new PostMethod(url);
		// 将表单的值放入postMethod中
		for (NameValuePair[] value : values) {
			postMethod.addParameters(value);
		}
		return getResponseStr(postMethod);
	}

	/**
	 * 发送post或get请求获取响应信息
	 * 
	 * @param method
	 *            http请求类型,post或get请求
	 * @return 服务器返回的信息
	 */
	public static String getResponseStr(HttpMethodBase method) {
		StringBuilder sb = new StringBuilder();
		BufferedReader dis = null;
		InputStream is = null;
		URI uri = null;
		try {
			uri = method.getURI();
			int statusCode = getHttpClient().executeMethod(method);
			if (statusCode != 200) {
				log.error("HttpClient Error : statusCode = " + statusCode + ", uri :" + uri);
				return "";
			}
			// 以流的行式读入，避免中文乱码
			is = method.getResponseBodyAsStream();
			dis = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String str = "";
			while ((str = dis.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception e) {
			log.error("network error:" + e.getMessage() + ", uri:" + uri);
		} finally {
			// 关闭连接
			method.releaseConnection();
			try {
				if (dis != null)
					dis.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

	/**
	 * 使用post方式调用
	 * 
	 * @param url
	 *            调用的URL
	 * @param values
	 *            传递的参数值List
	 * @param reqStr
	 *            请求中带的字符串信息
	 * @param contentType
	 *            Content-Type
	 * @return
	 */
	public static String httpPost(String url, NameValuePair[] values, String reqStr, String contentType) {
		RequestEntity reis = null;
		InputStream input = null;
		String s = "";
		PostMethod method = null;
		try {
			method = new PostMethod(url);
			method.addRequestHeader("Content-Type", contentType);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

			if (reqStr != null) {
				input = new ByteArrayInputStream(reqStr.getBytes("utf-8"));
				reis = new InputStreamRequestEntity(input);
				method.setRequestEntity(reis);
			}
			if (values != null) {
				method.setQueryString(values);
			}

			s = getResponseStr(method);
		} catch (Exception e) {
			log.error("exception occur, message:" + e.getMessage(), e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if(method != null) {
					method.releaseConnection();
				}

			} catch (IOException e) {
			}
		}
		return s;
	}

	/**
	 * 使用post方式调用
	 * 
	 * @param url
	 *            调用的URL
	 * @param values
	 *            传递的参数值
	 * @param xml
	 *            传递的xml参数
	 * @return
	 */
	public static String httpPost(String url, NameValuePair[] values, String xml) {
		return httpPost(url, values, xml, "text/xml;charset=UTF-8");
	}

	/**
	 * 使用post方式调用
	 * 
	 * @param url
	 *            调用的URL
	 * @param values
	 *            传递的参数值
	 * @param xml
	 *            传递的xml参数
	 * @return
	 */
	public static String httpPost(String url, String json) {
		return httpPost(url, null, json, "application/json;charset=UTF-8");
	}
	
	/**
	 * 使用post方式调用
	 * @param url 调用的URL
	 * @param values 传递的参数值List
	 * @param body 请求body信息
	 * @param headers 头信息列表
	 * @return
	 */
	public static String httpPost(String url, NameValuePair[] values, String body, Map<String, String> headers) {
		RequestEntity reis = null;
		InputStream input = null;
		String s = "";
		PostMethod method = null;
		try {
			method = new PostMethod(url);
			if (headers != null && headers.size() > 0) {
				for (Entry<String, String> entry : headers.entrySet()) {
					method.addRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

			if (body != null) {
				input = new ByteArrayInputStream(body.getBytes("utf-8"));
				reis = new InputStreamRequestEntity(input);
				method.setRequestEntity(reis);
			}
			if (values != null) {
				method.setQueryString(values);
			}

			s = getResponseStr(method);
		} catch (Exception e) {
			log.error("exception occur, message:" + e.getMessage(), e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if(method != null) {
					method.releaseConnection();
				}
			} catch (IOException e) {
			}
		}
		return s;
	}

	/**
	 * 将MAP<String, Object>转换成httpclient请求参数
	 * 
	 * @param paramMap
	 * @return
	 */
	public static NameValuePair[] praseParameterMap(Map<String, Object> paramMap) {
		if (paramMap == null)
			return null;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (String key : paramMap.keySet()) {
			Object value = paramMap.get(key);
			if (value instanceof String) {
				NameValuePair nvp = new NameValuePair();
				nvp.setName(key);
				nvp.setValue((String) value);
				list.add(nvp);
			} else if (value instanceof String[]) {
				String[] valueArray = (String[]) value;
				for (int i = 0; i < valueArray.length; i++) {
					NameValuePair nvp = new NameValuePair();
					nvp.setName(key);
					nvp.setValue(valueArray[i]);
					list.add(nvp);
				}
			} else if (value == null) {
				NameValuePair nvp = new NameValuePair();
				nvp.setName(key);
				nvp.setValue("");
				list.add(nvp);
			} else {
				NameValuePair nvp = new NameValuePair();
				nvp.setName(key);
				nvp.setValue(value.toString());
				list.add(nvp);
			}
		}
		NameValuePair[] nvps = new NameValuePair[list.size()];
		list.toArray(nvps);
		return nvps;
	}

	public static MultiThreadedHttpConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public static void setConnectionManager(MultiThreadedHttpConnectionManager connectionManager) {
		HttpSender.connectionManager = connectionManager;
	}

	public static int getMaxHostConnections() {
		return maxHostConnections;
	}

	public static void setMaxHostConnections(int maxHostConnections) {
		HttpSender.maxHostConnections = maxHostConnections;
	}

	public static int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public static void setMaxTotalConnections(int maxTotalConnections) {
		HttpSender.maxTotalConnections = maxTotalConnections;
	}

	public static void main(String args[]) {
		String url = "";
		NameValuePair[] values = new NameValuePair[2];
		NameValuePair nvp1 = new NameValuePair();
		nvp1.setName("a1");
		nvp1.setValue("b1");
		NameValuePair nvp2 = new NameValuePair();
		nvp2.setName("a2");
		nvp2.setValue("b2");
		values[0] = nvp1;
		values[1] = nvp2;

		String reqStr = "12313";
		String contentType = "application/json;charset=UTF-8";
		HttpSender.httpPost(url, values, reqStr, contentType);
	}
}
