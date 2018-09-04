package com.wondertek.mam.util.others;


import com.wondertek.mam.util.StringUtils;

/**
 * 读取配置文件
 * 
 * @author dszhou 2013-3-13
 * 
 */
public class ConfigUtil {
	public static final String configName = "/config.properties" ;
	
	public static String getProperty(String name) {
		return Configuration.getValue(configName, name);
	}

	public static String getPropertyByfileName(String fileName, String key) {
		if (fileName.equals("cas")) {
			return Configuration.getValue("/cas.properties", key);
		} else {
			return Configuration.getValue(fileName, key);
		}
	}
	
	public static String getString(String key)
	{
		return Configuration.getValue(configName, key);
	}

	public static String getString(String key, String def)
	{
		return Configuration.getValue(configName, key, def);
	}

	public static int getInt(String key)
	{
		return Configuration.getIntValue(configName, key);
	}

	public static int getInt(String key, int def)
	{
		return Configuration.getIntValue(configName, key, def);
	}
	
	public static boolean getBoolean(String key)
	{
		return StringUtils.isTrue(getString(key));
	}
	public static boolean getBoolean(String key, boolean def)
	{
		String value = getString(key) ;
		return StringUtils.isBlank(value) ? def : StringUtils.isTrue(value) ;
	}
	
	public static void reload()
	{
		Configuration.reload(configName);
	}

}
