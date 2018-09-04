package com.wondertek.mam.util.dataMigration;

import com.wondertek.mam.util.ST;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataMigrationFormater {
    private static Logger log = Logger.getLogger(DataMigrationFormater.class);
    public DataMigrationFormater(){}  // 无参构造
    public Object format(String str,Class typeCls){
        if(typeCls == null) {
            return null;
        }
        Object resultObj = null;
        //String typeFullName = typeCls.getName();
        String typeSimpleName = typeCls.getSimpleName();
        String typeName = typeSimpleName.toLowerCase();
        String methodName = "str2".concat(typeName.substring(0,1).toUpperCase()).concat(typeName.substring(1));
        try {
            Method method = this.getClass().getDeclaredMethod(methodName,String.class);
            resultObj = method.invoke(this,str);
        } catch (NoSuchMethodException e) {
            log.error("DataMigrationFormater:format --> method = this.getClass().getDeclaredMethod  Failure!:"+this.getClass().getName()+"不存在"+methodName+"方法!",e);
        } catch (InvocationTargetException e) {
            log.error("DataMigrationFormater:format --> resultObj = method.invoke(this,str)  Failure!:参数类型不匹配或modalCls没有无参构造又或此类型不支持反射实例对象",e);       //
        } catch (IllegalAccessException e) {
            log.error("DataMigrationFormater:format --> resultObj = method.invoke(this,str)  Failure!:类的域或方法为私有,不可达到",e);
        }
        return resultObj;
    }
    public <T> T format2(String str, Class<T> typeCls){
        T result = null;
        if(typeCls == null) {
            return result;
        }
        String typeSimpleName = typeCls.getSimpleName();
        String typeName = typeSimpleName.toLowerCase();
        String methodName = "str2".concat(typeName.substring(0,1).toUpperCase()).concat(typeName.substring(1));
        try {
            Method method = this.getClass().getDeclaredMethod(methodName,String.class);
            result = (T) method.invoke(this,str);
        } catch (NoSuchMethodException e) {
            log.error("DataMigrationFormater:format2 --> method = this.getClass().getDeclaredMethod  Failure!:"+this.getClass().getName()+"不存在"+methodName+"方法!",e);
        } catch (InvocationTargetException e) {
            log.error("DataMigrationFormater:format2 --> resultObj = method.invoke(this,str)  Failure!:参数类型不匹配或modalCls没有无参构造又或此类型不支持反射实例对象",e);       //
        } catch (IllegalAccessException e) {
            log.error("DataMigrationFormater:format2 --> resultObj = method.invoke(this,str)  Failure!:类的域或方法为私有,不可达到",e);
        }
        return result;
    }
    /*
     *  若对数据有特殊格式要求 可在子类中覆写以下相关类型的方法, 或在之类中新增方法对应类型的格式化方法:命名格式为 [str2*](注:"str2"加上类型名称,首字母大写)
     */
    protected String str2String(String str){
        return str;
    }
    protected Date str2Date(String str){
        if(!ST.isNull(str)){
            DateFormat df;
            str = str.trim();
            if(str.length()>11){
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }else {
                df = new SimpleDateFormat("yyyy-MM-dd");
            }
            try {
                return df.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    protected Integer str2Integer(String str){
        if(str.contains(".")){
            str = str.substring(0,str.lastIndexOf("."));
        }
        return ST.isNull(str)?0:Integer.valueOf(str);
    }
    protected Long str2Long(String str){
        if(str.contains(".")){
            str = str.substring(0,str.lastIndexOf("."));
        }
        return ST.isNull(str)?0L:Long.valueOf(str);
    }
    protected Boolean str2Boolean(String str){
        return ST.isNull(str)?false:Boolean.valueOf(str);
    }
    protected char[] str2Char(String str){
        return str.toCharArray();
    }
    protected byte[] str2Byte(String str){
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected Double str2Double(String str){
        return ST.isNull(str)?0:Double.valueOf(str);
    }
    protected Float str2Float(String str){
        return ST.isNull(str)?0f:Float.valueOf(str);
    }

}
