package com.wondertek.mam.util.others;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanUtil {
	private final static Log log = LogFactory.getLog(BeanUtil.class);

	public static Map<String, Object> bean2Map(Object o, Class c) {
		Field[] fields = c.getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(o));
			} catch (IllegalArgumentException e) {
				log.error("class:" + c.getName() + "property:"
						+ field.getName() + "set. error occar");
			} catch (IllegalAccessException e) {
				log.error("class:" + c.getName() + "property:"
						+ field.getName() + "set. error occar");
			}
		}
		return map;
	}
	//通过MAP解析出对象
	public static Object map2Bean(Object o, Class c, Map propertyMap)
			throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			String propertyName = pd.getName();
			if (propertyMap.containsKey(propertyName)) {
				try {
					pd.getWriteMethod()
							.invoke(o, propertyMap.get(pd.getName()));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					log.error("errorMsg:"+e.getMessage());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					log.error("errorMsg:"+e.getMessage());
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					log.error("errorMsg:"+e.getMessage());
				}
			}
		}
		return o;
	}
}
