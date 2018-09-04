package com.wondertek.mam.util.others;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件
 * 
 * 2012/2/6 Kyle Guo 新增getInstance方法
 */
public class Configuration2 {
	
	private Properties props;
	
	public Configuration2(String configFile){
		InputStream is = this.getClass().getResourceAsStream(configFile);
        try {
        	props = new Properties();
			props.load(is);
			is.close();
		} catch (IOException e) {
			System.out.println("no file: "+configFile+" -->"+e.getMessage());
		}
	}
	
	public String getProperty(String key) {
		    return (String) props.get(key);
	}
	
	public static Configuration c = null;
	
	public static Configuration getInstance(){
		
		if(c==null){
			c= new Configuration("/config.properties");
		}
		
		return c;
	}
}
