package com.wondertek.mam.util.others;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class ClassTools{
    public final static Log log = LogFactory.getLog(ClassTools.class);

    public static class Date {
        private java.lang.String format = "yyyy-MM-dd HH:mm:ss.SSS";
        private java.util.Date utilDate;
        private long time;
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;
        private int second;
        private int msecond;

        public Date() {
            this.utilDate = new java.util.Date();
            parseDate();
        }

        public Date(java.util.Date date) {
            this.utilDate = date;
            parseDate();
        }

        public Date(long time) {
            this.utilDate = new java.util.Date(time);
            parseDate();
        }

        @Override
        public java.lang.String toString() {
            return new SimpleDateFormat(format).format(this.utilDate);
        }

        private void parseDate() {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(this.utilDate);
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH) + 1;
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            this.minute = calendar.get(Calendar.MINUTE);
            this.second = calendar.get(Calendar.SECOND);
            this.msecond = calendar.get(Calendar.MILLISECOND);
            this.time = this.utilDate.getTime();
        }

        public java.util.Date getDate() {
            return utilDate;
        }

        public void setDate(java.util.Date utilDate) {
            this.utilDate = utilDate;
            parseDate();
        }

        public java.lang.String getFormat() {
            return format;
        }

        public void setFormat(java.lang.String format) {
            this.format = format;
        }

        public long getTime() {
            return time;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public int getSecond() {
            return second;
        }

        public int getMsecond() {
            return msecond;
        }
    }

    public static class Map implements java.util.Map{
        private java.util.Map utilMap = null;

        public Map(java.util.Map uMap){
            this.utilMap = uMap;
        }

        public static java.util.Map<java.lang.String,Object> stringKey2LowerCase(java.util.Map<java.lang.String,Object> map){
            if(map == null || map.size() <= 0) return  null;
            java.util.Map<java.lang.String,Object> result = new HashMap<java.lang.String, Object>();
            Iterator<java.lang.String> iterator = map.keySet().iterator();
            while (iterator.hasNext()){
                java.lang.String key = iterator.next();
                result.put(key.toLowerCase(),map.get(key));
            }
            return result;
        }

        public static List<java.util.Map<java.lang.String,Object>> stringKey2LowerCase(List<java.util.Map<java.lang.String,Object>> listMap){
            if(listMap == null || listMap.size() <= 0) return null;
            List<java.util.Map<java.lang.String,Object>> result = new ArrayList<java.util.Map<java.lang.String, Object>>();
            for(java.util.Map<java.lang.String,Object> map : listMap){
                map = stringKey2LowerCase(map);
                if(map != null){
                    result.add(map);
                }
            }
            return result;
        }
        @Override
        public int size() {
            return isNull()?0:this.utilMap.size();
        }

        @Override
        public boolean isEmpty() {
            return isNull()?true:this.utilMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return isNull()?false:this.utilMap.containsKey(key);
        }
        public boolean containsKeyIgnoreCase(Object key) {
            boolean result = false;
            if(!isNull()){
                if(key instanceof String){
                    result = this.utilMap.containsKey(key) || this.utilMap.containsKey(((java.lang.String) key).toLowerCase()) || this.utilMap.containsKey(((java.lang.String) key).toUpperCase());
                }else {
                    result = this.utilMap.containsKey(key);
                }
            }
            return result;
        }

        @Override
        public boolean containsValue(Object value) {
            return isNull()?false:this.utilMap.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return isNull()?null:this.utilMap.get(key);
        }

        public Object getIgnoreCase(Object key) {
            Object result = null;
            if(!isNull()){
                if(key instanceof String){
                    if((result = this.utilMap.get(key)) == null){
                        if((result = this.utilMap.get(((java.lang.String) key).toLowerCase())) == null) {
                            result = this.utilMap.get(((java.lang.String) key).toUpperCase());
                        }
                    }
                }else {
                    result = this.utilMap.get(key);
                }
            }
            return result;
        }

        @Override
        public Object put(Object key, Object value) {
            return isNull()?null:this.utilMap.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return isNull()?null:this.utilMap.remove(key);
        }

        @Override
        public void putAll(java.util.Map m) {
            if(!isNull()){
                this.utilMap.putAll(m);
            }
        }

        @Override
        public void clear() {
            if(!isNull()){
                this.utilMap.clear();
            }
        }

        @Override
        public Set keySet() {
            return isNull()?null:this.utilMap.keySet();
        }

        @Override
        public Collection values() {
            return isNull()?null:this.utilMap.values();
        }

        @Override
        public Set<Entry> entrySet() {
            return isNull()?null:this.utilMap.entrySet();
        }

        private boolean isNull(){
            return this.utilMap == null;
        }

        public java.util.Map getUtilMap() {
            return utilMap;
        }

        public void setUtilMap(java.util.Map utilMap) {
            this.utilMap = utilMap;
        }

        @Override
        public java.lang.String toString() {
            if(this.utilMap == null ) return "";
            return "Map{" +
                    "utilMap=" + utilMap +
                    '}';
        }
    }

    public static class Str {
        public static <T> T str2Number(String str,Class<T> cls){
            T l = null;
            Pattern pattern = Pattern.compile("\\d+");
            if(str != null && pattern.matcher(str).matches()){
                try {
                    Method valueOf ;
                    if ((valueOf = cls.getMethod("valueOf",String.class)) != null){
                        l = (T) valueOf.invoke(null,str);
                    }
                } catch (NoSuchMethodException e) {
                    log.error("ClassTools.Str.str2Number() --> "+ cls.getSimpleName() +" has not the method >>valueOf<< ",e);
                } catch (InvocationTargetException e) {
                    log.error(e);
                } catch (IllegalAccessException e) {
                    log.error(e);
                }
            }
            return l;
        }
    }
}
