package com.wondertek.mam.util;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

/**
 * 输出ELK日志
 * 日志级别：debug/info/warn/error/fatal/trace
 * @author chenyunxiao
 */
public class ELKLogUtil {
	private static final Log log = LogFactory.getLog(ELKLogUtil.class);
	
	/**
	 * 日志格式设定
	 * @param parameters
	 */
	private static String outputContent(Map<String,Object> parameters){
		try {
			HttpServletRequest request = null;
			String userName = null;//操作用户名
			String remoteIp = null;//操作用户对应IP
			String servletPath = null;//操作请求路径
			try {
				request = ServletActionContext.getRequest();
				if(request != null) {
					userName = StringUtils.trimToEmpty((String) request.getSession().getAttribute("_sso_username"));
					remoteIp = request.getRemoteAddr();
					servletPath = request.getServletPath();
				}
			} catch (Exception e) {
				//此处不设异常输出
			}
			
			String paramsStr = "";
			Iterator<String> it = parameters.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				paramsStr = paramsStr + key + "=" + StringUtil.object2String(parameters.get(key)) + "&";
			}
			if (!StringUtil.isNullStr(paramsStr) && paramsStr.trim().endsWith("&")) {
				paramsStr = paramsStr.substring(0, paramsStr.length() - 1);
			}
			
			StringBuilder message = new StringBuilder(200);
			message.append("ELK login user [" + StringUtil.null2Str(userName) + "] from IP [" + StringUtil.null2Str(remoteIp) + "] ");
			message.append("ServletPath [" + StringUtil.null2Str(servletPath) + "]. ");
			message.append("request params [" + paramsStr + "] ");
			
			return message.toString();
		} catch (Exception e) {
			//容错处理
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static void debug(Map<String,Object> parameters) {
		String output = outputContent(parameters);
		if(output != null)
			log.debug(output);
	}
	
	public static void info(Map<String,Object> parameters) {
		String output = outputContent(parameters);
		if(output != null)
			log.info(output);
	}
	
	public static void warn(Map<String,Object> parameters) {
		String output = outputContent(parameters);
		if(output != null)
			log.warn(output);
	}
	
	public static void error(Map<String,Object> parameters) {
		String output = outputContent(parameters);
		if(output != null)
			log.error(output);
	}
	
	public static void fatal(Map<String,Object> parameters) {
		String output = outputContent(parameters);
		if(output != null)
			log.fatal(output);
	}
	
	public static void trace(Map<String,Object> parameters) {
		String output = outputContent(parameters);
		if(output != null)
			log.trace(output);
	}
	
	/**
	 * 测试使用，慎用
	 */
	public static void call4TestOnly(String logStr) {
		log.info(logStr);
	}
}
