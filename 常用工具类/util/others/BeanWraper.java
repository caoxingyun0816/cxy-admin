package com.wondertek.mam.util.others;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 将map转化成对象，只转基本属性和日期，其它属性会被忽略掉
 * 
 * @author Administrator
 *
 * @param <T>
 */
public class BeanWraper<T>
{
	protected Class<T> type;

	public BeanWraper(final Class<T> type)
	{
		this.type = type;
	}
	
	private String dateTimePattern ;
	public T convertMap(Map<Object,Object> map) throws AgentEngineException
	{
		try
		{
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			T bean = type.newInstance(); 

			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++)
			{
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				if (map.containsKey(propertyName))
				{
					Object value = map.get(propertyName);
					if (value != null && value.toString().trim().length() > 0)
					{
						Object[] args = new Object[1];
						args[0] = convertType(descriptor.getPropertyType(),value);
						try
						{
							descriptor.getWriteMethod().invoke(bean, args);
						} catch (Exception e)
						{
						} 
					}
				}
			}
			return bean;
		} catch (Exception e)
		{
			throw new AgentEngineException("BeanWraper.convertMap",e);
		} 
	}
	
	private Object convertType (Class<?> cls,Object value)
	{
		if (cls == String.class)
			return value.toString();
		if (cls == Integer.class || "int".equals(cls.getName()))
			return Integer.valueOf(value.toString());
		if (cls == Long.class || "long".equals(cls.getName()))
			return Long.valueOf(value.toString());
		if (cls == Double.class|| "double".equals(cls.getName()))
			return Double.valueOf(value.toString());
		if (cls == Short.class|| "short".equals(cls.getName()))
			return Short.valueOf(value.toString());
		if (cls == Float.class|| "float".equals(cls.getName()))
			return Float.valueOf(value.toString());
		if (cls == Byte.class|| "byte".equals(cls.getName()))
			return Byte.valueOf(value.toString());
		if (cls == Boolean.class|| "boolean".equals(cls.getName()))
			return Boolean.valueOf(value.toString());
		if (cls == Date.class)
			return convertDate(value.toString());
		return value;
	}
	
	/**
	 * 将字符串转化成日期格式，
	 * 支持的格式：	yyyy-MM-dd HH:mm:ss
	 * 				yyyyMMddHHmmss
	 * 				yyyy-MM-dd
	 * 				yyyyMMdd
	 * @param date
	 * @return
	 */
	public Date convertDate(String dateStr)
	{
		if (StringUtils.isBlank(dateStr))
			return null;
		SimpleDateFormat sf = null;
		
		if (dateTimePattern != null)
			sf = new SimpleDateFormat(dateTimePattern);
		else if (dateStr.length() == 8)
			sf = new SimpleDateFormat("yyyyMMdd");
		else if (dateStr.length() == 10)
			sf = new SimpleDateFormat("yyyy-MM-dd");
		else if (dateStr.length() == 14)
			sf = new SimpleDateFormat("yyyyMMddHHmmss");
		else
			sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try
		{
			return sf.parse(dateStr);
		} catch (ParseException e)
		{
			return null;
		}
	}

	public String getDateTimePattern()
	{
		return dateTimePattern;
	}

	public void setDateTimePattern(String dateTimePattern)
	{
		this.dateTimePattern = dateTimePattern;
	}
}

