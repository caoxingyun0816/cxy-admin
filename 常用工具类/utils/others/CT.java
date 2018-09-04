package com.wondertek.mam.util.others;

import com.wondertek.mam.util.ST;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;



/**
 * Collection tool
 *    
 * CT  
 *   
 * Creator: eddie  
 * Mender:  eddie  
 * 2011-6-22 下午05:14:25  
 *   
 * @version 1.0.0  
 *
 */
@SuppressWarnings("unchecked")
public class CT {
    private static final Class collection = Collection.class;
    private static final Class iterator = Iterator.class;
	private static final Class map = Map.class;
	
	/**
	 * 将一个map的值映射到一个对象之中
	 * reflectMap2Bean  
	 * @param map
	 * @param bean
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException    
	 * void   
	 * @exception    
	 * @since  1.0.0
	 */
	public final static void reflectMap2Bean(Map<String,Object> map,Object bean) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		if (!CT.isEmpty(map)){
			Map.Entry<String, Object> item = null;
			Class cls = bean.getClass();
			Field field = null;

			for (Iterator<Map.Entry<String, Object>> it = CT.getIterator(map); it.hasNext();) {
				item = it.next();
				field = cls.getDeclaredField(item.getKey());
				if (field != null)
				{
					field.setAccessible(true);
					field.set(bean, field.getType().cast(item.getValue()));
				}
			}
		}
	}
	
	/**
     * 将集合内容连接成一个字符串
     * concat  
     * @param o,集合对象
     * @param concatStr,连接分隔符
     * @return    
     * String   
     * @exception    
     * @since  1.0.0
     */
	public final static String concat(Object o,String concatStr) {
    	if (isCollection(o)) {
    		StringBuilder str = new StringBuilder();
    		String tmp = null;
    		
    		for (Iterator it = getIterator(o); it.hasNext();) {
    			tmp = it.next().toString();
    			if (!ST.isNull(tmp)) {
    				str.append(tmp).append(concatStr);
    			}
    		}
    		String result = str.toString();
    		return ST.isNull(result) ? result :  result.substring(0, result.length() - 1);
    	} else {
    		return "";
    	}
    }
    
    /**
     * 将集合内容连接成一个字符串,使用默认分隔符-","
     * concat  
     * @param o
     * @return    
     * String   
     * @exception    
     * @since  1.0.0
     */
    public final static String concat(Object o) {
    	return concat(o,",");
    }
    
    /**
     * 判断集合类型和数组的对象是否为空或长度小于1
     * 支持接口类型
     * Collection 
     * Iterator
     * Map
     * Array
     * @param obj
     * @return boolean
     */
    public final static boolean isEmpty(Object obj)
    {
    	if (obj == null)
    		return true;
    	if (iterator.isInstance(obj))
    	{
    		return !((Iterator)obj).hasNext();
    	} else {
    		return getSize(obj) <= 0;
    	}
    }
    
    /**
     * 获取集合大小
     * getCollectionSize  
     * @param obj
     * @return    
     * int   
     * @exception    
     * @since  1.0.0
     */
    public final static int getSize(Object obj) {
    	
    	if (isCollection(obj)) {
    		if (collection.isInstance(obj))
        	{
        		return ((Collection)obj).size();
        	}
        	else if (iterator.isInstance(obj))
        	{
        		int i = 0;
        		for (Iterator it = (Iterator)obj;it.hasNext();) {
        			i++;
        			it.next();
        		}
        		return i;
        	}
        	else if (map.isInstance(obj))
        	{
        		return ((Map)obj).size();
        	}
        	else if (obj.getClass().isArray())
        	{
        		return (Array.getLength(obj));
        	}
    	} 
    	return 0;
    }
    
    /**
     * 判断对象是否为集合类型
     * isCollections  
     * @param col
     * @return    
     * boolean   
     * @exception    
     * @since  1.0.0
     */
    public final static boolean isCollection(Object col) {
    	return col == null ? false : (collection.isInstance(col) || iterator.isInstance(col) || map.isInstance(col) || col.getClass().isArray());
    }
    
    /**
     * 将集合对象以迭代器的方式返回
     * @param obj
     * @return
     * @throws UnsupportedOperationException
     */
	public final static Iterator getIterator(Object obj) throws UnsupportedOperationException{
    	if (collection.isInstance(obj))
    	{
    		return (((Collection)obj).iterator());
    	}
    	else if (iterator.isInstance(obj))
    	{
    		return (Iterator)obj;
    	}
    	else if (map.isInstance(obj))
    	{
    		return (((Map)obj).entrySet().iterator());
    	} else if (obj != null && obj.getClass().isArray()) {
    		return new _Iterator((Object[])obj);
    	} else {
    		return new _Iterator(null);
    	}
    }
    
    /**
     * 为数组类型对象包装一个迭代器
     * @author SunYue
     *
     */
    private final static class _Iterator implements Iterator {
    	private Object[] array = null;
    	private int index = 0;
    	private int size = 0;
    	
    	public  _Iterator(Object[] array) {
    		this.array = array;
    		this.size = array == null ? 0 : array.length;
    	}
    	
		@Override
		public boolean hasNext() {
			return index < size;
		}

		@Override
		public Object next() throws NoSuchElementException{
			return array[index++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
    }
    
    /**
     * 将字符串转换为对应的类型值
     * @param args,要转换类型的字符串数组
     * @param types,类型数组,数组长度应与字符串数组长度一致
     * @return
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
	public final static Object[] convertParameters(String[] args,Class[] types) throws NullPointerException,IndexOutOfBoundsException{
    	if (isEmpty(args) || isEmpty(types))
    		return new Object[0];
    	Object[] values = new Object[args.length];
    	for (int i = 0; i < args.length; i++) {
    		values[i] = types[i].cast(args[i]);
    	}
    	return values;
    }
    
	/**
	 * 在键-值对集合中根据值获取key
	 * @param value
	 * @return
	 */
	public final static List getKeys(Map map,Object value) {
		if (isEmpty(map))
			return new ArrayList(0);
		List keys = new ArrayList(0);
		Set set =  map.entrySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry)it.next();
			Object v = entry.getValue();
			if (ST.equals(value,v))
				keys.add(entry.getKey());
		}
		return keys;
	}
}
