package com.imooc.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by caoxingyun on 2018/8/2.
 */
public class JacksonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(JacksonUtil.class);

    /**
     * 将对象转化为json
     * @author caoxingyun
     * @param obj 待转化的对象
     * @return 当转化发生异常时返回null
     */
    public static String toJson(Object obj){
        if(obj == null){
            return null;
        }
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error(String.format("obj=[%s]", obj.toString()), e);
        }
        return null;
    }

    /**
     * 将json转化为对象
     * @param json json对象
     * @param clazz 待转化的对象类型
     * @return 当转化发生异常时返回null
     */
    public static <T> T fromJson(String json,Class<T> clazz){
        if(json == null){
            return null;
        }
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error(String.format("json=[%s]", json), e);
        }
        return null;
    }

    /**
     * 将json对象转化为集合类型
     * @param json json对象
     * @param collectionClazz 具体的集合类的class，如：ArrayList.class
     * @param clazz 集合内存放的对象的class
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> Collection<T> fromJson(String json, Class<? extends Collection> collectionClazz, Class<T> clazz){
        if(json == null){
            return null;
        }
        try {
            Collection<T> collection =  mapper.readValue(json, getCollectionType(collectionClazz,clazz));
            return collection;
        } catch (IOException e) {
            log.error(String.format("json=[%s]", json), e);
        }
        return null;
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
