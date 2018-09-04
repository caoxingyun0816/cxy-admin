package com.wondertek.mam.util.others;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public class NotifyRMSUtil {

	public static Log log = LogFactory.getLog(NotifyRMSUtil.class);
	public static final String POST_METHOD = "post";
	public static final String PUT_METHOD = "put";
	public static final String DELETE_METHOD = "delete";
	
	// RMS2消息通知防止并发N条新增消息
	public static Map<Long, Long> pubQueueMap = new HashMap<Long, Long> ();
	
	/**
	 * RMS通知
	 * @param httpURL      通知地址
	 * @param filePath     文件地址
	 * @param objType      通知数据类型
	 * @param objId        内容ID
	 * @param notifyType   通知请求类型
	 * @param isEmerg      是否紧急通知
	 * @return
	 */
	public static ErrorVO notifyRMS(String httpURL, String filePath, String objType, String pubObjId, String notifyType, Boolean isEmerg, Boolean isNeedObjectId) {
		ErrorVO errorVo = new ErrorVO();
		FileInputStream inputStream = null;
		if (!DELETE_METHOD.equals(notifyType)) {
			try {
				File file = new File(filePath);
				com.wondertek.mobilevideo.core.util.XmlUtil.openXml(filePath);
				inputStream = new FileInputStream(file);
			} catch (Exception e) {
				log.debug("Notify Checked File Error: " + e.getMessage());
				errorVo.setResourceID(pubObjId);
				errorVo.setMsgCode("5001");
				return errorVo;
			}
		}
		return inputStreamNotifyRMS(httpURL, inputStream, objType, pubObjId, notifyType, isEmerg, isNeedObjectId);
	}
	
	/**
	 * 
	 * RMS通知
	 * @param httpURL      通知地址
	 * @param inputStream  文件流
	 * @param objType      通知数据类型
	 * @param objId        内容ID
	 * @param notifyType   通知请求类型
	 * @param isEmerg      是否紧急通知
	 * @return
	 */
	public static ErrorVO inputStreamNotifyRMS(String httpURL, InputStream inputStream, String objType, String pubObjId, String notifyType, Boolean isEmerg, Boolean isNeedObjectId) {
		ErrorVO errorVo = new ErrorVO();
		Long startTime = System.currentTimeMillis();
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		// 读取数据超时时间
		client.getParams().setSoTimeout(60000); 
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);
		
		if (isEmerg)
			httpURL += "/emerg";
		httpURL += "/" + objType;
		if (isNeedObjectId)
			httpURL += "/" + pubObjId;
		
		// RMS消息通知防止并发N条新增消息
		if(notifyType != null && notifyType.trim().equalsIgnoreCase(NotifyRMSUtil.POST_METHOD)){
			Long publishTime = pubQueueMap.get(Long.valueOf(pubObjId));
			if(publishTime != null && publishTime > (startTime - 60000)){
				notifyType = NotifyRMSUtil.PUT_METHOD;
			}else{
				if(pubQueueMap != null && pubQueueMap.size() > 5000)
					pubQueueMap = new HashMap<Long, Long> ();
				pubQueueMap.put(Long.valueOf(pubObjId), startTime);
			}
		}
		log.info("Notify [URL='" + httpURL + "', notifyType='" + notifyType + "']");
		
		HttpMethod httpMethod = null;
		if (POST_METHOD.equals(notifyType.toLowerCase().trim())) {
			PostMethod postMethod = new PostMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			postMethod.setRequestEntity(reis);
			httpMethod = postMethod;
		} else if (PUT_METHOD.equals(notifyType.toLowerCase().trim())) {
			PutMethod putMethod = new PutMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			putMethod.setRequestEntity(reis);
			httpMethod = putMethod;
		} else if (DELETE_METHOD.equals(notifyType.toLowerCase().trim())) {
			DeleteMethod deleteMethod = new DeleteMethod(httpURL);
			httpMethod = deleteMethod;
		}

		try {
			httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			int retcode = client.executeMethod(httpMethod);
			if (retcode == HttpStatus.SC_OK) {
				try {
					return parseResponseStream(httpMethod.getResponseBodyAsStream());
				} catch (ParserConfigurationException e) {
					errorVo.setResourceID(pubObjId);
					errorVo.setMsgCode("5003");
					return errorVo;
				} catch (SAXException e) {
					errorVo.setResourceID(pubObjId);
					errorVo.setMsgCode("5004");
					return errorVo;
				} catch (IOException e) {
					errorVo.setResourceID(pubObjId);
					errorVo.setMsgCode("5005");
					return errorVo;
				}
			} else {
				errorVo.setResourceID(pubObjId);
				errorVo.setMsgCode(httpMethod.getStatusCode() + "");
				log.debug("New Notify Connect Server Back Error: [StatusCode='" + httpMethod.getStatusText() + "',StatusText='" + httpMethod.getStatusText() + "'");
				return errorVo;
			}
		} catch (Exception e) {
			log.debug("New Notify Connect Server Error: " + e.getMessage());
			errorVo.setResourceID(pubObjId);
			errorVo.setMsgCode("5002");
			return errorVo;
		} finally {
			pubQueueMap.remove(Long.valueOf(pubObjId));
			httpMethod.releaseConnection();
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("errorMsg:"+e.getMessage());
				}
			}
			log.info("New SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}
	
	public static ErrorVO inputStreamNotifyRMS(String httpURL, String filePath, Long pubObjId, String notifyType){
		ErrorVO errorVo = new ErrorVO();
		FileInputStream inputStream = null;
		if (!DELETE_METHOD.equals(notifyType)) {
			try {
				File file = new File(filePath);
				com.wondertek.mobilevideo.core.util.XmlUtil.openXml(filePath);
				inputStream = new FileInputStream(file);
			} catch (Exception e) {
				log.debug("Notify Checked File Error: " + e.getMessage());
				errorVo.setMsgCode("5001");
				return errorVo;
			}
		}
		
		Long startTime = System.currentTimeMillis();
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		// 读取数据超时时间
		client.getParams().setSoTimeout(60000); 
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);
		
		// RMS消息通知防止并发N条新增消息
		if(notifyType != null && notifyType.trim().equalsIgnoreCase(NotifyRMSUtil.POST_METHOD)){
			Long publishTime = pubQueueMap.get(pubObjId);
			if(publishTime != null && publishTime > (startTime - 60000)){
				notifyType = NotifyRMSUtil.PUT_METHOD;
			}else{
				if(pubQueueMap != null && pubQueueMap.size() > 5000)
					pubQueueMap = new HashMap<Long, Long> ();
				pubQueueMap.put(pubObjId, startTime);
			}
		}
		log.info("Notify [URL='" + httpURL + "', notifyType='" + notifyType + "']");

		HttpMethod httpMethod = null;
		if (POST_METHOD.equals(notifyType.toLowerCase().trim())) {
			PostMethod postMethod = new PostMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			postMethod.setRequestEntity(reis);
			httpMethod = postMethod;
		} else if (PUT_METHOD.equals(notifyType.toLowerCase().trim())) {
			PutMethod putMethod = new PutMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			putMethod.setRequestEntity(reis);
			httpMethod = putMethod;
		} else if (DELETE_METHOD.equals(notifyType.toLowerCase().trim())) {
			DeleteMethod deleteMethod = new DeleteMethod(httpURL);
			httpMethod = deleteMethod;
		}

		try {
			httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			int retcode = client.executeMethod(httpMethod);
			if (retcode == HttpStatus.SC_OK) {
				try {
					return parseResponseStream(httpMethod.getResponseBodyAsStream());
				} catch (ParserConfigurationException e) {
					errorVo.setMsgCode("5003");
					return errorVo;
				} catch (SAXException e) {
					errorVo.setMsgCode("5004");
					return errorVo;
				} catch (IOException e) {
					errorVo.setMsgCode("5005");
					return errorVo;
				}
			} else {
				errorVo.setMsgCode(httpMethod.getStatusCode() + "");
				log.debug("New Notify Connect Server Back Error: [StatusCode='" + httpMethod.getStatusText() + "',StatusText='" + httpMethod.getStatusText() + "'");
				return errorVo;
			}
		} catch (Exception e) {
			log.debug("New Notify Connect Server Error: " + e.getMessage());
			errorVo.setMsgCode("5002");
			return errorVo;
		} finally {
			pubQueueMap.remove(pubObjId);
			httpMethod.releaseConnection();
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("errorMsg:"+e.getMessage());
				}
			}
			log.info("New SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}

	/**
	 * @param httpURL
	 * @param filePath
	 * @param notifyType
	 * @return
	 */
	public static ErrorVO inputStreamNotifyRMS(String httpURL, String filePath, String notifyType){
		ErrorVO errorVo = new ErrorVO();
		FileInputStream inputStream = null;
		if (!DELETE_METHOD.equals(notifyType)) {
			try {
				File file = new File(filePath);
				com.wondertek.mobilevideo.core.util.XmlUtil.openXml(filePath);
				inputStream = new FileInputStream(file);
			} catch (Exception e) {
				log.debug("Notify Checked File Error: " + e.getMessage());
				errorVo.setMsgCode("5001");
				return errorVo;
			}
		}
		
		Long startTime = System.currentTimeMillis();
		
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		// 读取数据超时时间
		client.getParams().setSoTimeout(60000); 
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);
		log.info("Notify [URL='" + httpURL + "', notifyType='" + notifyType + "']");

		HttpMethod httpMethod = null;
		if (POST_METHOD.equals(notifyType.toLowerCase().trim())) {
			PostMethod postMethod = new PostMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			postMethod.setRequestEntity(reis);
			httpMethod = postMethod;
		} else if (PUT_METHOD.equals(notifyType.toLowerCase().trim())) {
			PutMethod putMethod = new PutMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			putMethod.setRequestEntity(reis);
			httpMethod = putMethod;
		} else if (DELETE_METHOD.equals(notifyType.toLowerCase().trim())) {
			DeleteMethod deleteMethod = new DeleteMethod(httpURL);
			httpMethod = deleteMethod;
		}

		try {
			httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			int retcode = client.executeMethod(httpMethod);
			if (retcode == HttpStatus.SC_OK) {
				try {
					return parseResponseStream(httpMethod.getResponseBodyAsStream());
				} catch (ParserConfigurationException e) {
					errorVo.setMsgCode("5003");
					return errorVo;
				} catch (SAXException e) {
					errorVo.setMsgCode("5004");
					return errorVo;
				} catch (IOException e) {
					errorVo.setMsgCode("5005");
					return errorVo;
				}
			} else {
				errorVo.setMsgCode(httpMethod.getStatusCode() + "");
				log.debug("New Notify Connect Server Back Error: [StatusCode='" + httpMethod.getStatusText() + "',StatusText='" + httpMethod.getStatusText() + "'");
				return errorVo;
			}
		} catch (Exception e) {
			log.debug("New Notify Connect Server Error: " + e.getMessage());
			errorVo.setMsgCode("5002");
			return errorVo;
		} finally {
			httpMethod.releaseConnection();
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("errorMsg:"+e.getMessage());
				}
			}
			log.info("New SendHttpRequest RequestTime["
					+ (System.currentTimeMillis() - startTime) / 1000 + "ms]");
		}
	}
	
	/**
	 * RMS通知
	 * @param httpURL       通知地址
	 * @param filePath      文件地址
	 * @param objType       通知数据类型
	 * @param objId         内容ID
	 * @param notifyType    通知请求类型
	 * @param isEmerg       是否紧急通知
	 * @return
	 * @throws IOException
	 */
	public static ErrorVO notifyStrRMS(String httpURL, String str, String objType, String pubObjId, String notifyType, Boolean isEmerg, Boolean isNeedObjectId) throws IOException {
		ErrorVO errorVo = new ErrorVO();
		if (isEmerg)
			httpURL += "/emerg";
		httpURL += "/" + objType;
		if (isNeedObjectId)
			httpURL += "/" + pubObjId;
		log.info("Notify [URL='" + httpURL + "', notifyType='" + notifyType + "']");

		InputStream inputStream = null;
		if (!DELETE_METHOD.equals(notifyType)) {
			try {
				byte[] bytes = str.getBytes("UTF-8"); // 存储为内存字节
				inputStream = new ByteArrayInputStream(bytes);
			} catch (Exception e) {
				log.debug("Notify Checked File Error: " + e.getMessage());
				errorVo.setResourceID(pubObjId);
				errorVo.setMsgCode("5001");
				return errorVo;
			} finally {
				inputStream.close();

			}
		}

		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		// 读取数据超时时间
		client.getParams().setSoTimeout(60000); 
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);

		HttpMethod httpMethod = null;
		if (POST_METHOD.equals(notifyType.toLowerCase().trim())) {
			PostMethod postMethod = new PostMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			postMethod.setRequestEntity(reis);
			httpMethod = postMethod;
		} else if (PUT_METHOD.equals(notifyType.toLowerCase().trim())) {
			PutMethod putMethod = new PutMethod(httpURL);
			RequestEntity reis = new InputStreamRequestEntity(inputStream);
			putMethod.setRequestEntity(reis);
			httpMethod = putMethod;
		} else if (DELETE_METHOD.equals(notifyType.toLowerCase().trim())) {
			DeleteMethod deleteMethod = new DeleteMethod(httpURL);
			httpMethod = deleteMethod;
		}

		try {
			httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			int retcode = client.executeMethod(httpMethod);
			if (retcode == HttpStatus.SC_OK) {
				try {
					return parseResponseStream(httpMethod
							.getResponseBodyAsStream());
				} catch (ParserConfigurationException e) {
					errorVo.setResourceID(pubObjId);
					errorVo.setMsgCode("5003");
					return errorVo;
				} catch (SAXException e) {
					errorVo.setResourceID(pubObjId);
					errorVo.setMsgCode("5004");
					return errorVo;
				} catch (IOException e) {
					errorVo.setResourceID(pubObjId);
					errorVo.setMsgCode("5005");
					return errorVo;
				}

			} else {
				errorVo.setResourceID(pubObjId);
				errorVo.setMsgCode(httpMethod.getStatusCode() + "");
				return errorVo;
			}
		} catch (Exception e) {
			log.debug("New Notify Connect Server Error: " + e.getMessage());
			errorVo.setResourceID(pubObjId);
			errorVo.setMsgCode("5002");
			return errorVo;
		} finally {
			httpMethod.releaseConnection();
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("errorMsg:"+e.getMessage());
				}
			}
		}
	}
	
	
	/**
	 * 栏目内容批量移动
	 * @param httpURL 通知URL
	 * @param srcNodeId  源栏目ID
	 * @param targetNodeId  目标栏目ID
	 * @return
	 * @throws IOException
	 */
	public static ErrorVO notifyStrRMS(String httpURL, Long srcNodeId, Long targetNodeId) throws IOException {
		ErrorVO errorVo = new ErrorVO();
		httpURL+= "/clmnchange/" + srcNodeId + "/" + targetNodeId;
		log.info("Notify [URL='" + httpURL + "'");
		
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		// 读取数据超时时间
		client.getParams().setSoTimeout(60000); 
		client.setConnectionTimeout(60000);
		client.setTimeout(60000);
		
		HttpMethod httpMethod = new PutMethod(httpURL);
		
		try {
			httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			int retcode = client.executeMethod(httpMethod);
			if (retcode == HttpStatus.SC_OK) {
				try {
					return parseResponseStream(httpMethod
							.getResponseBodyAsStream());
				} catch (ParserConfigurationException e) {
					errorVo.setResourceID(srcNodeId+","+targetNodeId);
					errorVo.setMsgCode("5003");
					return errorVo;
				} catch (SAXException e) {
					errorVo.setResourceID(srcNodeId+","+targetNodeId);
					errorVo.setMsgCode("5004");
					return errorVo;
				} catch (IOException e) {
					errorVo.setResourceID(srcNodeId+","+targetNodeId);
					errorVo.setMsgCode("5005");
					return errorVo;
				}
				
			} else {
				errorVo.setResourceID(srcNodeId+","+targetNodeId);
				errorVo.setMsgCode(httpMethod.getStatusCode() + "");
				return errorVo;
			}
		} catch (Exception e) {
			log.debug("New Notify Connect Server Error: " + e.getMessage());
			errorVo.setResourceID(srcNodeId+","+targetNodeId);
			errorVo.setMsgCode("5002");
			return errorVo;
		} finally {
			httpMethod.releaseConnection();
		}
	}

	public static ErrorVO parseResponseStream(InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException {
		ErrorVO errorVO = new ErrorVO();
		StringBuffer sb = new StringBuffer();
		int ch = -1;
		while ((ch = inputStream.read()) != -1)
			sb.append((char) ch);
		log.info("New Notify Back Message: " + sb.toString());
		Document doc = com.wondertek.mobilevideo.core.util.XmlUtil.getDocByStr(sb.toString());
		NodeList nodes = doc.getElementsByTagName("Result");

		if (nodes == null || nodes.getLength() == 0) {
			throw new SAXException("parse New Notify Back XML Data Exception");
		}

		if (nodes.item(0) instanceof Element) {
			Element elm = (Element) nodes.item(0);
			for (Node child = elm.getFirstChild(); child != null; child = child
					.getNextSibling()) {
				if (child.getNodeName() != null
						&& child.getFirstChild() != null
						&& child.getFirstChild().getNodeValue() != null) {
					if (child.getNodeName().equalsIgnoreCase("ResourceID")) {
						errorVO.setResourceID(child.getFirstChild()
								.getNodeValue().trim());
					} else if (child.getNodeName().equalsIgnoreCase("MsgCode")) {
						errorVO.setMsgCode(child.getFirstChild().getNodeValue()
								.trim());
					}
				}
			}
		}

		if (errorVO == null || errorVO.getMsgCode() == null) {
			throw new SAXException("parse New Notify Back XML Data Exception");
		}
		return errorVO;
	}
}
