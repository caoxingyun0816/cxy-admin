package com.wondertek.mam.util.others;

import com.wondertek.mam.util.file.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration
{
	private static Map<String, Configuration> pools = new HashMap<String,Configuration>();
	private Properties props;
	private String configName ;
	
	public Configuration(String configFile)
	{
		this.props = new Properties();
		this.configName = configFile;
		loadProp();
	}
	private Configuration loadProp()
	{
		InputStream is = FileHelper.getInputStream8Cls(this.configName);
		this.props.clear();
		try
		{
			if (is != null)
				this.props.load(is);
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
			}
		}
		pools.put(this.configName, this);
		return this;
	}
	
	public String getProperty(String key)
	{
		return props.getProperty(key);
	}
	public String getProperty(String key, String def)
	{
		return props.containsKey(key) ? props.getProperty(key) : def;
	}
	public int getIntProperty(String key)
	{
		return this.getIntProperty(key, 0);
	}
	public int getIntProperty(String key, int def)
	{
		try
		{
			return Integer.parseInt(props.getProperty(key));
		}catch(Exception e)
		{
			return def ;
		}
	}
	
	public static void reloadAll()
	{
		for(Configuration cfg : pools.values())
			cfg.loadProp();
	}
	public static Configuration reload(String configName)
	{
		return pools.containsKey(configName) ? pools.get(configName).loadProp() : new Configuration(configName);
	}
	public static String getValue(String configName, String key)
	{
		return getConfiguration(configName).getProperty(key);
	}
	public static String getValue(String configName, String key, String def)
	{
		return getConfiguration(configName).getProperty(key, def);
	}
	public static int getIntValue(String configName, String key)
	{
		return getConfiguration(configName).getIntProperty(key);
	}
	public static int getIntValue(String configName, String key, int def)
	{
		return getConfiguration(configName).getIntProperty(key, def);
	}
	public static Configuration getConfiguration(String configName)
	{
		return pools.containsKey(configName) ? pools.get(configName) : new Configuration(configName);
	}
}	
