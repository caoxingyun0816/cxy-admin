package com.wondertek.mam.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.wondertek.mobilevideo.core.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JavaIdentifierTransformer;
import org.apache.commons.beanutils.BeanUtils;

/**
 * 序列化对象为JSON格式 遵循JSON组织公布标准
 * 
 * @date 2008/05/07
 * @version 1.0.0
 * @updated by liuping
 * @update date 2016-11-25
 * @version 1.0.0.1
 */
public class JsonUtil {
	public static String timeFormats = "yyyy-MM-dd HH:mm:ss"; //new SimpleDateFormat(timeFormats);

	/**
	 * @param obj
	 *            任意对象
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String object2json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		}else if (obj instanceof Integer
				|| obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append(string2json(obj.toString()));
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2json((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		} else if (obj instanceof Date) {
			return "\"" + new SimpleDateFormat(timeFormats).format(obj) + "\"";
		} else {
			json.append(bean2json(obj));
		}
		return json.toString();
	}

	/**
	 * @param obj
	 *            任意对象
	 * @return String
	 */
	public static String updatedObject2json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String) {
			//如果为URL的字符串就不进行字符转义
			String eL = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
			if(Pattern.compile(eL).matcher((String)obj).matches()){
				json.append("\"").append(obj.toString()).append("\"");
			}else{
				json.append("\"").append(string2json(obj.toString())).append("\"");
			}

		}else if (obj instanceof Integer
				|| obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append(string2json(obj.toString()));
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2jsonUpdated((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2jsonUpdated((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		} else if (obj instanceof Date) {
			return "\"" + new SimpleDateFormat(timeFormats).format(obj) + "\"";
		} else {
			json.append(bean2json(obj));
		}
		return json.toString();
	}

	/**
	 * @param bean
	 *            bean对象
	 * @return String
	 */
	public static String bean2json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2json(props[i].getName());
					String value = object2json(props[i].getReadMethod().invoke(
							bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param bean
	 *            bean对象
	 * @return String
	 */
	public static String bean2jsonUpdated(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = updatedObject2json(props[i].getName());
					String value = updatedObject2json(props[i].getReadMethod().invoke(
							bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	public static String bean2jsonNoSet(Object bean, String beanName) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2jsonNoSet(beanName + "."
							+ props[i].getName());
					String value = object2jsonNoSet(props[i].getReadMethod()
							.invoke(bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param bean
	 *            bean对象
	 * @return String
	 */
	public static String bean2jsonNoSet(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2jsonNoSet(props[i].getName());
					String value = object2jsonNoSet(props[i].getReadMethod()
							.invoke(bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	public static String list2jsonNoSet(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2jsonNoSet(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	@SuppressWarnings("unchecked")
	public static String object2jsonNoSet(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String || obj instanceof Integer
				|| obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append("\"").append("").append("\"");
		} else if (obj instanceof List) {
			json.append("\"").append("").append("\"");
		} else if (obj instanceof HashSet) {
			json.append("\"").append("").append("\"");
		} else if (obj instanceof Set) {
			json.append("\"").append("").append("\"");
		} else if (obj instanceof Date) {
			return "\"" + new SimpleDateFormat(timeFormats).format(obj) + "\"";
		} else {
			json.append(bean2jsonNoSet(obj));
		}
		return json.toString();
	}

	/**
	 * @param bean
	 *            bean对象 beanName 对象名称
	 * @return String
	 */
	public static String bean2json(Object bean, String beanName) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2json(beanName + "."
							+ props[i].getName());
					String value = object2json(props[i].getReadMethod().invoke(
							bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	public static String list2json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	public static String list2jsonUpdated(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(updatedObject2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}
	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	public static String list2json4Object2json(List<?> list) {
		StringBuilder json = new StringBuilder();
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
		return json.toString();
	}

	public static String list2jsonFistBig(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2jsonFirstBig(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param array
	 *            对象数组
	 * @return String
	 */
	public static String array2json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param map
	 *            map对象
	 * @return String
	 */
	public static String map2json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2json(key));
				json.append(":");
				json.append(object2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param map
	 *            map对象
	 * @return String
	 */
	public static String map2jsonUpdated(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(updatedObject2json(key));
				json.append(":");
				json.append(updatedObject2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param set
	 *            集合对象
	 * @return String
	 */
	public static String set2json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param s
	 *            参数
	 * @return String
	 */
	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	public static String simpleMap2Json(Map<String, String> rawData,
			String keyFlag, String valueFlag) {
		StringBuilder sb = new StringBuilder();
		sb.append("{data:[");
		for (Object oneKey : rawData.keySet()) {
			sb.append("{'" + keyFlag + "':'" + oneKey + "','" + valueFlag
					+ "':'" + rawData.get(oneKey) + "'},");
		}
		if (sb.lastIndexOf(",") != -1) {
			sb.deleteCharAt(sb.lastIndexOf(","));
		} // 去掉最后一个逗号
		sb.append("]}");
		return sb.toString();
	}

	public static String createSuccesJson() {
		return "{success:true,message:''}";
	}

	public static String createSuccesJson(String message) {
		return "{success:true,message:'" + message + "'}";
	}

	public static String createFailureJson(String message) {
		return "{success:false,message:'" + message + "'}";
	}

	public static String createFailureJson() {
		return "{success:false}";
	}

	/**
	 * json 字符串转成List<Map>格试
	 * @param json 字符串
	 * @return 
	 */
	@SuppressWarnings({"unchecked" })
	public static List<Map<String,Object>> json2List(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
        if(json==null || json.equals("") || json.equals("[]")){
        	return null;
        }
		JSONArray array = JSONArray.fromObject(json);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (Iterator iter = array.iterator(); iter.hasNext();) {
			JSONObject jsonObject = (JSONObject) iter.next();

			Object obj = JSONObject.toBean(jsonObject, map.getClass());
			map = (Map<String, Object>) obj;
			list.add(map);

		}
		return list;
	}

	/**
	 * 将json字符串转化为一个对象集合
	 * json2List  
	 * @param json
	 * @param clazz
	 * @return    
	 * List<Object>   
	 * @exception    
	 * @since  1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static List json2List(String json,Class<?> clazz) {
        if(StringUtil.isNull(json) || json.equals("[]")){
        	return null;
        }
		List<Object> list = new ArrayList<Object>();
		for (Iterator<JSONObject> iter = JSONArray.fromObject(json).iterator(); iter.hasNext();) {
			list.add(JSONObject.toBean(iter.next(), clazz));
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static List jsonFirstBig2List(String json,Class<?> clazz) {
        if(StringUtil.isNull(json) || json.equals("[]")){
        	return null;
        }
        JsonConfig config = new JsonConfig();
        config.setJavaIdentifierTransformer(new JavaIdentifierTransformer() {

            @Override
            public String transformToJavaIdentifier(String str) {
                char[] chars = str.toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                return new String(chars);
            }

        });
        config.setRootClass(clazz);
		List<Object> list = new ArrayList<Object>();
		for (Iterator<JSONObject> iter = JSONArray.fromObject(json).iterator(); iter.hasNext();) {
			list.add(JSONObject.toBean(iter.next(), config));
		}
		return list;
	}
	
	/**
	 * 将json字符串转化为一个对象
	 * json2List  
	 * @param jsonStr
	 * @param clazz
	 * @return    
	 * List<Object>   
	 * @exception    
	 * @since  1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static Object json2Object(String jsonStr, Class<?> clazz) {
        if(StringUtil.isNullStr(jsonStr) || "[]".equals(jsonStr)){
        	return null;
        }
		//在转对象之前将所有的特殊字符进行替换处理
		/*jsonStr = jsonStr.replace(">", "&gt;");
		jsonStr = jsonStr.replace("<", "&lt;");
		jsonStr = jsonStr.replace(" ", "&nbsp;");
		jsonStr = jsonStr.replace("\"", "&quot;");
		jsonStr = jsonStr.replace("\'", "&#39;");
		jsonStr = jsonStr.replace("\\", "\\\\");//对斜线的转义
		jsonStr = jsonStr.replace("\n", "\\n");
		jsonStr = jsonStr.replace("\r", "\\r");*/
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		Object obj = JSONObject.toBean(jsonObject, clazz);
		return obj;
	}
	
	/**
	 * 将json字符串转化为一个对象
	 * json2List  
	 * @param json
	 * @param config
	 * @param clazz
	 * @return    
	 * List<Object>   
	 * @exception    
	 * @since  1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static Object json2Object(String json, JsonConfig config, Class<?> clazz) {
        if(StringUtil.isNull(json) || json.equals("[]")){
        	return null;
        }
        JSONObject jobj = JSONObject.fromObject(json, config);
		Object obj = JSONObject.toBean(jobj, clazz);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static Object json2ObjectFirstBig(String json,Class<?> clazz) {
        if(StringUtil.isNull(json) || json.equals("[]")){
        	return null;
        }
        JSONObject jobj = JSONObject.fromObject(json);
        JsonConfig config = new JsonConfig();
        config.setJavaIdentifierTransformer(new JavaIdentifierTransformer() {

            @Override
            public String transformToJavaIdentifier(String str) {
                char[] chars = str.toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                return new String(chars);
            }

        });
        config.setRootClass(clazz);
        Object obj = JSONObject.toBean(jobj, config);
		return obj;
	}
	
	/*
	 * 对象中含有list
	 */
	public static Object json2ObjectFirstBig(String json,Class<?> clazz, Map classMap) {
        if(StringUtil.isNull(json) || json.equals("[]")){
        	return null;
        }
        JSONObject jobj = JSONObject.fromObject(json);
        JsonConfig config = new JsonConfig();
        config.setJavaIdentifierTransformer(new JavaIdentifierTransformer() {

            @Override
            public String transformToJavaIdentifier(String str) {
                char[] chars = str.toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                return new String(chars);
            }

        });
        config.setRootClass(clazz);
        config.setClassMap(classMap);
        Object obj = JSONObject.toBean(jobj, config);
		return obj;
	}
	
	/**
	 * @param bean
	 *         
	 * 只有基本属性和list属性第一个字符转成大写
	 *	其他类型转第一字符大写需要复写
	 * @return String
	 */
	public static String bean2jsonFirstBig(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2json(props[i].getName());
					char[] chars = name.toCharArray();
	                chars[1] = Character.toUpperCase(chars[1]);
	                name = new String(chars);
					String value = object2jsonFirstBig(props[i].getReadMethod().invoke(
							bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}
	
	/*
	 * 只有基本属性和list属性第一个字符转成大写
	 */
	public static String object2jsonFirstBig(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		}else if (obj instanceof Integer
				|| obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append(string2json(obj.toString()));
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2jsonFistBig((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		} else if (obj instanceof Date) {
			return "\"" + new SimpleDateFormat(timeFormats).format(obj) + "\"";
		} else {
			json.append(bean2jsonFirstBig(obj));
		}
		return json.toString();
	}


	/*********************************/
	/***
	 * 将List对象序列化为JSON文本
	 */
	public static <T> String toJSONString(List<T> list)
	{
		JSONArray jsonArray = JSONArray.fromObject(list);

		return jsonArray.toString();
	}

	/***
	 * 将对象序列化为JSON文本
	 * @param object
	 * @return
	 */
	public static String toJSONString(Object object)
	{
		JSONArray jsonArray = JSONArray.fromObject(object);

		return jsonArray.toString();
	}

	/***
	 * 将JSON对象数组序列化为JSON文本
	 * @param jsonArray
	 * @return
	 */
	public static String toJSONString(JSONArray jsonArray)
	{
		return jsonArray.toString();
	}

	/***
	 * 将JSON对象序列化为JSON文本
	 * @param jsonObject
	 * @return
	 */
	public static String toJSONString(JSONObject jsonObject)
	{
		return jsonObject.toString();
	}

	/***
	 * 将对象转换为List对象
	 * @param object
	 * @return
	 */
	public static List toArrayList(Object object)
	{
		List arrayList = new ArrayList();

		JSONArray jsonArray = JSONArray.fromObject(object);

		Iterator it = jsonArray.iterator();
		while (it.hasNext())
		{
			JSONObject jsonObject = (JSONObject) it.next();

			Iterator keys = jsonObject.keys();
			while (keys.hasNext())
			{
				Object key = keys.next();
				Object value = jsonObject.get(key);
				arrayList.add(value);
			}
		}

		return arrayList;
	}

	/***
	 * 将对象转换为Collection对象
	 * @param object
	 * @return
	 */
	public static Collection toCollection(Object object)
	{
		JSONArray jsonArray = JSONArray.fromObject(object);

		return JSONArray.toCollection(jsonArray);
	}

	/***
	 * 将对象转换为JSON对象数组
	 * @param object
	 * @return
	 */
	public static JSONArray toJSONArray(Object object)
	{
		return JSONArray.fromObject(object);
	}

	/***
	 * 将对象转换为JSON对象
	 * @param object
	 * @return
	 */
	public static JSONObject toJSONObject(Object object)
	{
		return JSONObject.fromObject(object);
	}

	/***
	 * 将对象转换为HashMap
	 * @param object
	 * @return
	 */
	public static HashMap toHashMap(Object object)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		JSONObject jsonObject = JsonUtil.toJSONObject(object);
		Iterator it = jsonObject.keys();
		while (it.hasNext())
		{
			String key = String.valueOf(it.next());
			Object value = jsonObject.get(key);
			data.put(key, value);
		}

		return data;
	}

	/***
	 * 将对象转换为List>
	 * @param object
	 * @return
	 */
	// 返回非实体类型(Map)的List
	public static List<Map<String, Object>> toList(Object object)
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = JSONArray.fromObject(object);
		for (Object obj : jsonArray)
		{
			JSONObject jsonObject = (JSONObject) obj;
			Map<String, Object> map = new HashMap<String, Object>();
			Iterator it = jsonObject.keys();
			while (it.hasNext())
			{
				String key = (String) it.next();
				Object value = jsonObject.get(key);
				map.put((String) key, value);
			}
			list.add(map);
		}
		return list;
	}

	/***
	 * 将JSON对象数组转换为传入类型的List
	 * @param
	 * @param jsonArray
	 * @param objectClass
	 * @return
	 */
	public static <T> List<T> toList(JSONArray jsonArray, Class<T> objectClass)
	{
		return JSONArray.toList(jsonArray, objectClass);
	}

	/***
	 * 将对象转换为传入类型的List
	 * @param
	 * @param
	 * @param objectClass
	 * @return
	 */
	public static <T> List<T> toList(Object object, Class<T> objectClass)
	{
		JSONArray jsonArray = JSONArray.fromObject(object);

		return JSONArray.toList(jsonArray, objectClass);
	}

	/***
	 * 将JSON对象转换为传入类型的对象
	 * @param
	 * @param jsonObject
	 * @param beanClass
	 * @return
	 */
	public static <T> T toBean(JSONObject jsonObject, Class<T> beanClass)
	{
		return (T) JSONObject.toBean(jsonObject, beanClass);
	}

	/***
	 * 将将对象转换为传入类型的对象
	 * @param
	 * @param object
	 * @param beanClass
	 * @return
	 */
	public static <T> T toBean(Object object, Class<T> beanClass)
	{
		JSONObject jsonObject = JSONObject.fromObject(object);

		return (T) JSONObject.toBean(jsonObject, beanClass);
	}

	/***
	 * 将JSON文本反序列化为主从关系的实体
	 *  泛型T 代表主实体类型
	 *  泛型D 代表从实体类型
	 * @param jsonString JSON文本
	 * @param mainClass 主实体类型
	 * @param detailName 从实体类在主实体类中的属性名称
	 * @param detailClass 从实体类型
	 * @return
	 */
	public static <T, D> T toBean(String jsonString, Class<T> mainClass,
								  String detailName, Class<D> detailClass)
	{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray = (JSONArray) jsonObject.get(detailName);

		T mainEntity = JsonUtil.toBean(jsonObject, mainClass);
		List<D> detailList = JsonUtil.toList(jsonArray, detailClass);

		try
		{
			BeanUtils.setProperty(mainEntity, detailName, detailList);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("主从关系JSON反序列化实体失败！");
		}

		return mainEntity;
	}

	/***
	 * 将JSON文本反序列化为主从关系的实体
	 *  泛型T 代表主实体类型
	 *  泛型D1 代表从实体类型
	 *  泛型D2 代表从实体类型
	 * @param jsonString JSON文本
	 * @param mainClass 主实体类型
	 * @param detailName1 从实体类在主实体类中的属性
	 * @param detailClass1 从实体类型
	 * @param detailName2 从实体类在主实体类中的属性
	 * @param detailClass2 从实体类型
	 * @return
	 */
	public static <T, D1, D2> T toBean(String jsonString, Class<T> mainClass,
									   String detailName1, Class<D1> detailClass1, String detailName2,
									   Class<D2> detailClass2)
	{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray1 = (JSONArray) jsonObject.get(detailName1);
		JSONArray jsonArray2 = (JSONArray) jsonObject.get(detailName2);

		T mainEntity = JsonUtil.toBean(jsonObject, mainClass);
		List<D1> detailList1 = JsonUtil.toList(jsonArray1, detailClass1);
		List<D2> detailList2 = JsonUtil.toList(jsonArray2, detailClass2);

		try
		{
			BeanUtils.setProperty(mainEntity, detailName1, detailList1);
			BeanUtils.setProperty(mainEntity, detailName2, detailList2);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("主从关系JSON反序列化实体失败！");
		}

		return mainEntity;
	}

	/***
	 * 将JSON文本反序列化为主从关系的实体
	 *  泛型T 代表主实体类型
	 *  泛型D1 代表从实体类型
	 *  泛型D2 代表从实体类型
	 * @param jsonString JSON文本
	 * @param mainClass 主实体类型
	 * @param detailName1 从实体类在主实体类中的属性
	 * @param detailClass1 从实体类型
	 * @param detailName2 从实体类在主实体类中的属性
	 * @param detailClass2 从实体类型
	 * @param detailName3 从实体类在主实体类中的属性
	 * @param detailClass3 从实体类型
	 * @return
	 */
	public static <T, D1, D2, D3> T toBean(String jsonString,
										   Class<T> mainClass, String detailName1, Class<D1> detailClass1,
										   String detailName2, Class<D2> detailClass2, String detailName3,
										   Class<D3> detailClass3)
	{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray1 = (JSONArray) jsonObject.get(detailName1);
		JSONArray jsonArray2 = (JSONArray) jsonObject.get(detailName2);
		JSONArray jsonArray3 = (JSONArray) jsonObject.get(detailName3);

		T mainEntity = JsonUtil.toBean(jsonObject, mainClass);
		List<D1> detailList1 = JsonUtil.toList(jsonArray1, detailClass1);
		List<D2> detailList2 = JsonUtil.toList(jsonArray2, detailClass2);
		List<D3> detailList3 = JsonUtil.toList(jsonArray3, detailClass3);

		try
		{
			BeanUtils.setProperty(mainEntity, detailName1, detailList1);
			BeanUtils.setProperty(mainEntity, detailName2, detailList2);
			BeanUtils.setProperty(mainEntity, detailName3, detailList3);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("主从关系JSON反序列化实体失败！");
		}

		return mainEntity;
	}

	/***
	 * 将JSON文本反序列化为主从关系的实体
	 * @param jsonString JSON文本
	 * @param mainClass 主实体类型
	 * @param detailClass 存放了多个从实体在主实体中属性名称和类型
	 * @return
	 */
	public static <T> T toBean(String jsonString, Class<T> mainClass,
							   HashMap<String, Class> detailClass)
	{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		T mainEntity = JsonUtil.toBean(jsonObject, mainClass);
		for (Object key : detailClass.keySet())
		{
			try
			{
				Class value = (Class) detailClass.get(key);
				BeanUtils.setProperty(mainEntity, key.toString(), value);
			}
			catch (Exception ex)
			{
				throw new RuntimeException("主从关系JSON反序列化实体失败！");
			}
		}
		return mainEntity;
	}
}