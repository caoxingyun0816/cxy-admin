package com.wondertek.mam.util.others;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

/**   
 * spring-bean实例上下文工具类   
 * AppContextUtils  
 *   
 * Creator: sunyue  
 * 2012-6-1 上午09:45:28  
 *   
 * @version 1.0.0  
 *    
 */
public class AppContextUtils {
	private static ApplicationContext ctx;
	private static Map<String,String> contextParams = new HashMap<String,String>();
	
	/**
	 * 设置应用上下文对象
	 * setApplicationContext  
	 * @param context    
	 * void   
	 * @exception    
	 * @since  1.0.0
	 */
	public static void setApplicationContext(ApplicationContext context) {
		ctx = context;
	}
	
	/**
	 * 获取应用上下文对象
	 * getApplicationContext  
	 * @return    
	 * ApplicationContext   
	 * @exception    
	 * @since  1.0.0
	 */
	public static ApplicationContext getApplicationContext(){
		return ctx;
	}
	
	/**
	 * 根据bean的id获取一个实例
	 * getBean  
	 * @param id
	 * @return    
	 * Object   
	 * @exception    
	 * @since  1.0.0
	 */
	public static Object getBean(String id) {
		return ctx.getBean(id);
	}

	/**
	 * 获取系统上下文参数值
	 * getContextParam  
	 * @param key
	 * @return    
	 * String   
	 * @exception    
	 * @since  1.0.0
	 */
	public static String getContextParam(String key) {
		return contextParams.get(key);
	}

	/**
	 * 设置系统上下文参数
	 * setContextParam  
	 * @param key
	 * @param value    
	 * void   
	 * @exception    
	 * @since  1.0.0
	 */
	public static void setContextParam(String key,String value) {
		AppContextUtils.contextParams.put(key, value);
	}
}
