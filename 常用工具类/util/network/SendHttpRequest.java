package com.wondertek.mam.util.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.json.JSONUtil;
import org.codehaus.jettison.json.JSONObject;
import org.xml.sax.SAXException;

import com.wondertek.mam.util.others.CmsUtil;

public class SendHttpRequest {
	public static Log log = LogFactory.getLog(SendHttpRequest.class);

	public static final String HTTP_NOTIFYNUM = "notifynum";
	public static final String HTTP_NOTIFYTYPE = "notifytype";
	public static final String HTTP_FILENAME = "filename";

	// RMS消息通知防止并发N条新增消息
	public static Map<Long, Long> pubQueueMap = new HashMap<Long, Long>();

	public static String send(String httpURL, Map<String, String> map,
			String filePath) throws Exception {
		Long startTime = System.currentTimeMillis();
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");
		client.getParams().setSoTimeout(60000);
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);

		// RMS消息通知防止并发N条新增消息
		if (map.get(HTTP_NOTIFYTYPE) != null
				&& map.get(HTTP_NOTIFYTYPE).trim().equalsIgnoreCase("A")) {
			Long publishTime = pubQueueMap.get(Long.valueOf(map
					.get(HTTP_NOTIFYNUM)));
			if (publishTime != null && publishTime > (startTime - 60000)) {
				map.put(HTTP_NOTIFYTYPE, "U");
			} else {
				if (pubQueueMap != null && pubQueueMap.size() > 5000)
					pubQueueMap = new HashMap<Long, Long>();
				pubQueueMap.put(Long.valueOf(map.get(HTTP_NOTIFYNUM)),
						startTime);
			}
		}
		log.info("Notify [URL='" + httpURL + "', pubObjId='"
				+ map.get(HTTP_NOTIFYNUM) + "', notifyType='"
				+ map.get(HTTP_NOTIFYTYPE) + "']");

		PostMethod method = null;
		method = new PostMethod(httpURL);
		method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
		method.addRequestHeader(HTTP_NOTIFYNUM, map.get(HTTP_NOTIFYNUM));
		method.addRequestHeader(HTTP_NOTIFYTYPE, map.get(HTTP_NOTIFYTYPE));
		method.addRequestHeader(HTTP_FILENAME, map.get(HTTP_FILENAME));

		FileInputStream fis = null;
		RequestEntity reis = null;
		try {
			File file = new File(filePath);
			com.wondertek.mobilevideo.core.util.XmlUtil.openXml(filePath);
			fis = new FileInputStream(file);
			reis = new InputStreamRequestEntity(fis);
			method.setRequestEntity(reis);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());

			int retcode = client.executeMethod(method);
			if (retcode == HttpStatus.SC_MOVED_PERMANENTLY
					|| retcode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = method.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.info("The page was redirected to:" + location);
					return "1:errorCode_";
				} else {
					log.error("Location field value is null.");
					return "1:errorCode_";
				}
			} else if (retcode == HttpStatus.SC_OK) {
				String responseBody = method.getResponseBodyAsString();
				log.info("New Notify Back Message: " + responseBody);
				return responseBody;
			} else {
				return "1:errorCode_" + method.getStatusCode() + " errorCause_"
						+ method.getStatusText();
			}
		} finally {
			//RMS消息通知防止并发N条新增消息 如果返回则删除 
			pubQueueMap.remove(Long.valueOf((map.get(HTTP_NOTIFYNUM)==null||"".equals(map.get(HTTP_NOTIFYNUM)))?"-1":map.get(HTTP_NOTIFYNUM)));
			method.releaseConnection();
			if (fis != null) {
				fis.close();
				fis = null;
			}
			log.info("SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}

	public static String sendStr(String httpURL, String str)
			throws HttpException, IOException, ParserConfigurationException,
			SAXException {

		Long startTime = System.currentTimeMillis();

		String url = httpURL;
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");
		client.getParams().setSoTimeout(60000); // 读取数据超时时间

		PostMethod method = null;
		method = new PostMethod(url);
		method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
		ByteArrayInputStream fis = null;
		RequestEntity reis = null;
		try {
			byte[] bytes = str.getBytes("UTF-8"); // 存储为内存字节
			fis = new ByteArrayInputStream(bytes); // 存储为输入输出流
			reis = new InputStreamRequestEntity(fis);
			method.setRequestEntity(reis);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());

			int retcode = client.executeMethod(method);
			if (retcode == HttpStatus.SC_MOVED_PERMANENTLY
					|| retcode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = method.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.info("The page was redirected to:" + location);
					return "1:errorCode_";
				} else {
					log.error("Location field value is null.");
					return "1:errorCode_";
				}
			} else if (retcode == HttpStatus.SC_OK) {
				String responseBody = method.getResponseBodyAsString();
				return responseBody;
			} else {
				return "1:errorCode_" + method.getStatusCode() + " errorCause_"
						+ method.getStatusText();
			}
		} finally {
			method.releaseConnection();
			if (fis != null) {
				fis.close();
				fis = null;
			}
			log.info("SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}

	@SuppressWarnings("deprecation")
	public static String sendMessage(String httpURL, String message)
			throws Exception {
		Long startTime = System.currentTimeMillis();
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");
		client.getParams().setSoTimeout(30000); // 读取数据超时时间
		client.setConnectionTimeout(30000);
		client.setTimeout(30000);

		log.info("SendHttpRequest[URL: " + httpURL + ", Message: " + message
				+ "]");
		PostMethod method = null;
		method = new PostMethod(httpURL);
		method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
		ByteArrayInputStream fis = null;
		RequestEntity reis = null;
		try {
			byte[] bytes = message.getBytes("UTF-8"); // 存储为内存字节
			fis = new ByteArrayInputStream(bytes); // 存储为输入输出流
			reis = new InputStreamRequestEntity(fis);
			method.setRequestEntity(reis);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			int retcode = client.executeMethod(method);
			if (retcode == HttpStatus.SC_OK) {
				String responseBody = method.getResponseBodyAsString();
				return responseBody;
			}
			return CmsUtil.getErrorText("error.notify.timeout",
					new Object[] { httpURL });
		} catch (Exception e) {
			throw e;
		} finally {
			method.releaseConnection();
			if (fis != null) {
				fis.close();
				fis = null;
			}
			log.info("SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}

	public static void send(String httpURL, String str) throws Exception {

		Long startTime = System.currentTimeMillis();
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");
		client.getParams().setSoTimeout(30000); // 读取数据超时时间
		client.setConnectionTimeout(30000);
		client.setTimeout(30000);

		PostMethod method = null;
		method = new PostMethod(httpURL);
		method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
		ByteArrayInputStream fis = null;
		RequestEntity reis = null;
		try {
			byte[] bytes = str.getBytes("UTF-8"); // 存储为内存字节
			fis = new ByteArrayInputStream(bytes); // 存储为输入输出流
			reis = new InputStreamRequestEntity(fis);
			method.setRequestEntity(reis);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			client.executeMethod(method);
		} catch (Exception e) {
			throw e;
		} finally {
			method.releaseConnection();
			if (fis != null) {
				fis.close();
				fis = null;
			}
			log.info("SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}

	public static String sendStr(String httpURL, Map<String, String> map,
			String str) throws HttpException, IOException,
			ParserConfigurationException, SAXException {

		Long startTime = System.currentTimeMillis();

		String url = httpURL;
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");
		client.getParams().setSoTimeout(60000); // 读取数据超时时间

		PostMethod method = null;
		method = new PostMethod(url);
		method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
		method.addRequestHeader("notifynum", map.get("notifynum"));
		method.addRequestHeader("notifytype", map.get("notifytype"));
		method.addRequestHeader("filename", map.get("filename"));

		ByteArrayInputStream fis = null;
		RequestEntity reis = null;
		try {
			byte[] bytes = str.getBytes("UTF-8"); // 存储为内存字节
			fis = new ByteArrayInputStream(bytes); // 存储为输入输出流
			reis = new InputStreamRequestEntity(fis);
			method.setRequestEntity(reis);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());

			int retcode = client.executeMethod(method);
			if (retcode == HttpStatus.SC_MOVED_PERMANENTLY
					|| retcode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = method.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.info("The page was redirected to:" + location);
					return "1:errorCode_";
				} else {
					log.error("Location field value is null.");
					return "1:errorCode_";
				}
			} else if (retcode == HttpStatus.SC_OK) {
				String responseBody = method.getResponseBodyAsString();
				return responseBody;
			} else {
				return "1:errorCode_" + method.getStatusCode() + " errorCause_"
						+ method.getStatusText();
			}
		} finally {
			method.releaseConnection();
			if (fis != null) {
				fis.close();
				fis = null;
			}
			log.info("SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}

	/**
	 * 带参数的请求URL
	 * 
	 * @param httpURL
	 *            请求url
	 * @param paramsMap
	 *            参数名和参数值map
	 * @author duguocheng
	 */
	public static String send(String httpURL, Map<String, String> paramsMap)
			throws HttpException, IOException {
		Long startTime = System.currentTimeMillis();
		String url = httpURL;
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");
		client.getParams().setSoTimeout(60000); // 读取数据超时时间

		PostMethod method = null;
		method = new PostMethod(url);
		method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");

		if (paramsMap != null) {
			Set<String> keySet = paramsMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String paramName = iterator.next();
				String paramValue = paramsMap.get(paramName);
				method.addParameter(paramName, paramValue);
			}
		}

		InputStream responseIS = null;
		BufferedReader responesBR = null;
		InputStreamReader responseISR = null;
		try {
			int retcode = client.executeMethod(method);
			if (retcode == HttpStatus.SC_MOVED_PERMANENTLY
					|| retcode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = method.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.info("The page was redirected to:" + location);
					return "1:errorCode_";
				} else {
					log.error("Location field value is null.");
					return "1:errorCode_";
				}
			} else if (retcode == HttpStatus.SC_OK) {
				responseIS = method.getResponseBodyAsStream();
				responseISR = new InputStreamReader(responseIS);
				responesBR = new BufferedReader(responseISR);
				String temp = null;
				StringBuffer sb = new StringBuffer();
				while ((temp = responesBR.readLine()) != null) {
					sb.append(temp);
				}
				if (responesBR != null)
					responesBR.close();
				String responseBody = sb.toString();
				log.info("responseBody is " + responseBody);
				return responseBody;
			} else {
				return "1:errorCode_" + method.getStatusCode() + " errorCause_"
						+ method.getStatusText();
			}
		} finally {
			if (responesBR != null)
				responesBR.close();
			if (responseISR != null)
				responseISR.close();
			if (responseIS != null)
				responseIS.close();

			if (method != null)
				method.releaseConnection();
			log.info("SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}

	public static Long sendMessageToCdn(String httpURL,
			Map<String, Object> messageMap) {
		Long startTime = System.currentTimeMillis();
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");
		client.getParams().setSoTimeout(60000);
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);

		PostMethod method = null;
		method = new PostMethod(httpURL);
		method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
		RequestEntity reis = null;
		try {
			String message = JSONUtil.serialize(messageMap);
			log.info("SendHttpRequest CDN[URL: " + httpURL + ", Message: "
					+ message + "]");
			reis = new InputStreamRequestEntity(new ByteArrayInputStream(
					message.getBytes()));
			method.setRequestEntity(reis);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			int retcode = client.executeMethod(method);
			if (retcode == HttpStatus.SC_OK) {
				String returnCode = method.getResponseBodyAsString();
				log.info("Request returnCode: [" + returnCode + "]");
				JSONObject jo = new JSONObject(returnCode);
				System.out.println("Res_Code: " + jo.getString("Res_Code"));
				if (jo.getString("Res_Code") != null
						&& NumberUtils.isNumber(jo.getString("Res_Code")) == true) {
					return Long.valueOf(jo.getString("Res_Code"));
				} else {
					return Long.valueOf(2002);
				}
			}

			log.info("Exception returnCode: [" + Long.valueOf(9999) + "]");
			return Long.valueOf(9999);
		} catch (Exception e) {
			log.info("Exception returnCode: [" + Long.valueOf(2003) + "]");
			return Long.valueOf(2003);
		} finally {
			method.releaseConnection();
			if (reis != null)
				reis = null;
			log.info("SendHttpRequest CDN RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}
	
	
	/**
	 * post 发送http请求
	 * @param url
	 * @param xml
	 * @param timeout
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static String httpPostXmlString(String url, String xml) throws HttpException, IOException {
		
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		client.getParams().setSoTimeout(60000);
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);
		
		RequestEntity reis = null;
		InputStream input = null;
		InputStream is = null;
		BufferedReader dis = null;
		PostMethod method = null;
		StringBuilder sb = new StringBuilder();
		try {
			method = new PostMethod(url);
			method.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

			if (xml != null) {
				input = new ByteArrayInputStream(xml.getBytes("utf-8"));
				reis = new InputStreamRequestEntity(input);
				method.setRequestEntity(reis);
			}
			
			/*int retcode = client.executeMethod(method);
			if (retcode == HttpStatus.SC_MOVED_PERMANENTLY
					|| retcode == HttpStatus.SC_MOVED_TEMPORARILY) {
				System.out.println("&&&&&&&&&&&&&& retcode : " +retcode);
				// 从头中取出转向的地址
				Header locationHeader = method.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.info("The page was redirected to:" + location);
					return "1:errorCode_";
				} else {
					log.error("Location field value is null.");
					return "1:errorCode_";
				}
			} else if (retcode == HttpStatus.SC_OK) {
				System.out.println("&&&&&&&&&&&&&& ok ");
				is = method.getResponseBodyAsStream();
				dis = new BufferedReader(new InputStreamReader(is));
				String temp = null;
				while ((temp = dis.readLine()) != null) {
					sb.append(temp);
				}

				return sb.toString();
			} else {
				System.out.println("&&&&&&&&&&&&&& other... ");
				return "1:errorCode_" + method.getStatusCode() + " errorCause_"
						+ method.getStatusText();
			}
			*/
			
			int retcode = client.executeMethod(method);
			log.info(" request return code :  " + retcode);
			
            // 以流的行式读入，避免中文乱码
            is = method.getResponseBodyAsStream();
            dis = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String str = "";
            while ((str = dis.readLine()) != null) {
                sb.append(str);
            }
            
		}finally {
			method.releaseConnection();
            try {
                if (dis != null)
                    dis.close();
                if (is != null)
                    is.close();
                if (input != null)
                    input.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
		}
		return sb.toString();
	}

	/**
	 * 发送http请求
	 * 
	 * @param url
	 *            请求地址
	 * @param timeout
	 * @param reqXml
	 * @return
	 * @throws IOException
	 */
	public static String sendHttp(String url, int timeout) throws IOException {

		StringBuffer strBuffer = new StringBuffer();
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		BufferedReader rufferedReader = null;

		try {
			URL serverUrl = new URL(url);
			httpURLConnection = (HttpURLConnection) serverUrl.openConnection();
			httpURLConnection.setConnectTimeout(timeout);
			httpURLConnection.setReadTimeout(timeout);
			// httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.getOutputStream().flush();
			httpURLConnection.getOutputStream().close();
			if (HttpURLConnection.HTTP_OK == httpURLConnection
					.getResponseCode()) {
				inputStream = httpURLConnection.getInputStream();
				rufferedReader = new BufferedReader(new InputStreamReader(
						inputStream, "UTF-8"));
				String str = null;
				while ((str = rufferedReader.readLine()) != null) {
					strBuffer.append(str);

				}
			}

		} catch (IOException e) {
			throw e;

		} finally {
			if (null != rufferedReader) {
				rufferedReader.close();
			}
			if (null != inputStream) {
				inputStream.close();
			}

			if (null != httpURLConnection) {
				httpURLConnection.disconnect();
			}
			httpURLConnection = null;
			rufferedReader = null;
			inputStream = null;
		}

		return strBuffer.toString();
	}
}