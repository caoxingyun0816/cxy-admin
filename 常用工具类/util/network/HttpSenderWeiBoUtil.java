package com.wondertek.mam.util.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import com.wondertek.mobilevideo.core.util.StringUtil;

public class HttpSenderWeiBoUtil {

	private static MultiThreadedHttpConnectionManager connectionManager;
	private static HttpClient client;
	/**
	 * maximum number of connections allowed per host
	 */
	private static int maxHostConnections = 1000;//TODO:数值待定
	
	/**
	 * maximum number of connections allowed overall
	 */
	private static int maxTotalConnections = 5000;//TODO:数值待定
	
	/**
	 * the timeout until a connection is etablished
	 */
	private static int connectionTimeOut = 1000;//TODO:数值待定
	
	private static int soTimeOut = 4000;
	
	static {
		connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setDefaultMaxConnectionsPerHost(maxHostConnections);
		params.setMaxTotalConnections(maxTotalConnections);
		params.setConnectionTimeout(connectionTimeOut);
		params.setSoTimeout(soTimeOut);
		connectionManager.setParams(params);
		client = new HttpClient(connectionManager);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
	}
	

	public static Logger log = Logger.getLogger(HttpSenderWeiBoUtil.class);
	
	public static byte[] httpPost(String url, Map<String, Object> headerParam,
			Map<String, Object> requestParam,HttpServletResponse response) {
		PostMethod postMethod = new PostMethod(url);
		if (headerParam != null && !headerParam.isEmpty()) {
			for (Entry<String, Object> entry : headerParam.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				postMethod.addRequestHeader(key, StringUtil.null2Str(value));
			}
		}
		if (requestParam != null && !requestParam.isEmpty()) {
			for (Entry<String, Object> entry : requestParam.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				postMethod.addParameter(key, String.valueOf(value));
			}
		}
		return getResponseStr(postMethod,response);
	}
	
	/**
	 * 发送post或get请求获取响应信息
	 * @param method	http请求类型,post或get请求
	 * @return			服务器返回的信息
	 */
	public static byte[] getResponseStr(HttpMethodBase method,HttpServletResponse response) {
		URI uri = null;
		byte[] ret = null;
		try {
			uri = method.getURI();
			int statusCode = client.executeMethod(method);
			if (statusCode != 200) {
				log.error("HttpClient Error : statusCode = " + statusCode + ", uri :" + uri );
				String str = "{\"uri\":\"" + uri + "\" ,\"statusCode\":\"" + statusCode + "\" ,\"desc\":\"" + "interface error" + "\"}";
				ret = str.getBytes();
		        return ret;
//				return null;
			}
			//以流的行式读入，避免中文乱码
			InputStream is = method.getResponseBodyAsStream();
			ret = input2byte(is);
			Header []headers = method.getResponseHeaders();
			if(headers!=null && headers.length>0){
				for(Header header:headers){
					if (!"Transfer-Encoding".equalsIgnoreCase(header.getName())) {
						response.addHeader(header.getName(), header.getValue());
					}
				}
			}
			String charset = method.getResponseCharSet();
			long contentLength = method.getResponseContentLength();
			response.setCharacterEncoding(charset);
			response.setContentLength((int)contentLength);
			
		} catch (Exception e) {
			if ((e.getClass().getName()).indexOf("ConnectTimeoutException") > -1) {
				log.error("ConnectTimeoutException:" + "userid:"
						+ method.getRequestHeader("contact").getValue()
						+ ",mobile:"
						+ method.getRequestHeader("identify").getValue()
						+ ",request uri:" + uri);
			} else if ((e.getClass().getName())
					.indexOf("SocketTimeoutException") > -1) {
				log.error("SocketTimeoutException:" + "userid:"
						+ method.getRequestHeader("contact").getValue()
						+ ",mobile:"
						+ method.getRequestHeader("identify").getValue()
						+ ",request uri:" + uri);
			}else{
				log.error(e.getClass().getName()+":" + "userid:"
						+ method.getRequestHeader("contact").getValue()
						+ ",mobile:"
						+ method.getRequestHeader("identify").getValue()
						+ ",request uri:" + uri);
			}
			log.error("调用远程出错;发生网络异常!uri:" + uri);
			String str = "{\"uri\":\"" + uri + "\" ,\"desc\":\"" + "time out" + "\"}";
			ret = str.getBytes();
			e.printStackTrace();
			return ret;
		} finally {
			// 关闭连接
			method.releaseConnection();
		}
		return ret;
	}
	
    public static final byte[] input2byte(InputStream inStream) throws IOException {  
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
		byte[] buff = new byte[100];  
		int rc = 0;  
		while ((rc = inStream.read(buff, 0, 100)) > 0) {  
		    swapStream.write(buff, 0, rc);  
		}  
		byte[] in2b = swapStream.toByteArray();  
		return in2b;  
	}  
}
