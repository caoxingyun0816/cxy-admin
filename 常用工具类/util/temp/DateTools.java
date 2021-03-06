package com.wondertek.mam.util.temp;

/**
 * Simple to Introduction
 *
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [一句话描述该类的功能]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Since [${date} ${time}]
 * @UpdateUser: [${user}]
 * @UpdateDate: [${date} ${time}]
 * @UpdateRemark: [说明本次修改内容]
 * @Version: [v1.0]
 */

/**
 * <p>Title: 日期常用工具方法类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) </p>
 * <p>Company: </p>
 * @author zf1212 2006-4-10
 * @version 1.0
 */
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 日期常用工具方法类
 */
public final class DateTools {

    public static String getCurDateTimeStr()
    {
        Date newdate = new Date() ;
        long datetime = newdate.getTime() ;
        Timestamp timestamp = new Timestamp(datetime) ;
        String str = timestamp.toString();
        return new StringBuffer()
                .append(datetime)
                .toString();
    }

    public static String getCurDateTime()
    {
        Date newdate = new Date() ;
        long datetime = newdate.getTime() ;
        Timestamp timestamp = new Timestamp(datetime) ;
        return (timestamp.toString()).substring(0,19);
    }

    public static String getCurrentDate()
    {
        Date newdate = new Date() ;
        long datetime = newdate.getTime() ;
        Timestamp timestamp = new Timestamp(datetime) ;
        String currentdate = (timestamp.toString()).substring(0,4) + "-" + (timestamp.toString()).substring(5,7) + "-" +(timestamp.toString()).substring(8,10);
        return currentdate;
    }
    //返回"yyyyMMdd"格式日期
    public static String getDateYYYYMMDD()
    {
        Date newdate = new Date() ;
        long datetime = newdate.getTime() ;
        Timestamp timestamp = new Timestamp(datetime) ;
        String currentdate = (timestamp.toString()).substring(0,4)  + (timestamp.toString()).substring(5,7)  +(timestamp.toString()).substring(8,10);
        return currentdate;
    }
    //返回"yyMMdd"格式日期
    public static String getDateYYMMDD()
    {
        Date newdate = new Date() ;
        long datetime = newdate.getTime() ;
        Timestamp timestamp = new Timestamp(datetime) ;
        String currentdate = (timestamp.toString()).substring(2,4)  + (timestamp.toString()).substring(5,7)  +(timestamp.toString()).substring(8,10);
        return currentdate;
    }
    //返回"yyyy-MM-dd"格式日期
    public static String getDate(Date newdate){
        long datetime = newdate.getTime() ;
        Timestamp timestamp = new Timestamp(datetime) ;
        String currentdate = (timestamp.toString()).substring(0,4) + "-" + (timestamp.toString()).substring(5,7) + "-" +(timestamp.toString()).substring(8,10);
        return currentdate;

    }
    //获取开始时间和结束时间之间的天数
    public static long getDisDays (String datebegin, String dateend) {
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Date dateBegin=sdf.parse(datebegin);
            Date dateEnd=sdf.parse(dateend);
            return (dateEnd.getTime()-dateBegin.getTime())/(3600*24*1000) + 1;
        } catch (Exception e) {
            return 0;
        }
    }



    public static String getCurrentTime()
    {
        Date newdate = new Date() ;
        long datetime = newdate.getTime() ;
        Timestamp timestamp = new Timestamp(datetime) ;
        String currenttime = (timestamp.toString()).substring(11,13) + ":" + (timestamp.toString()).substring(14,16) + ":" +(timestamp.toString()).substring(17,19);
        return currenttime;
    }
    /**
     * 计算2个日期之间间隔天数方法
     *
     * @param d1    The first Calendar.
     * @param d2    The second Calendar.
     *
     * @return      天数
     */
    public static long getDaysBetween (java.util.Calendar d1, java.util.Calendar d2) {
        return (d1.getTime().getTime()-d2.getTime().getTime())/(3600*24*1000);
    }
    /**
     * 计算2个日期之间间隔天数方法
     *
     * @param d1    	The first Calendar.
     * 				格式yyyy-MM-dd
     * @param d2    	The second Calendar.
     *
     * @return      天数
     */
    public static long getDaysBetween (String d1, String d2) {
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Date dt1=sdf.parse(d1);
            Date dt2=sdf.parse(d2);
            return (dt1.getTime()-dt2.getTime())/(3600*24*1000);
        } catch (Exception e) {
            return 0;
        }

    }
    /**
     * @param d1
     * @param d2
     * @param onlyWorkDay 是否只计算工作日
     * @return 计算两个日期之间的时间间隔(d1-d2)，可选择是否计算工作日
     */
    public static long getDaysBetween (String d1,String d2,boolean onlyWorkDay) {
        if(!onlyWorkDay){
            return getDaysBetween(d1,d2);
        }else{
            long days=0;
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
            try {
                Date dt1=sdf.parse(d1);
                Date dt2=sdf.parse(d2);
                days= (dt1.getTime()-dt2.getTime())/(3600*24*1000);
                for(calendar.setTime(dt1);!calendar.getTime().before(dt2);calendar.add(Calendar.DAY_OF_YEAR,-1)){
                    int week=calendar.get(Calendar.DAY_OF_WEEK);
                    if(week == Calendar.SUNDAY || week == Calendar.SATURDAY){
                        days--;
                    }
                }
                if(days<0){
                    days=0;
                }
            } catch (Exception e) {}
            return days;
        }
    }
    /**
     * @param date
     * @return 判断日期是否为工作日(周六和周日为非工作日)
     */
    public static boolean isWorkDay(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int week=calendar.get(Calendar.DAY_OF_WEEK);
        if(week == Calendar.SUNDAY || week == Calendar.SATURDAY){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 计算两个时间之间的间隔  单位：分钟(minutes)
     * 格式 yyyy-MM-dd/HH:mm:ss
     * */
    public static long getMinutesBetween(String s1, String s2) {
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd/HH:mm:ss");
        try {
            Date dt1=sdf.parse(s1);
            Date dt2=sdf.parse(s2);
            return (dt1.getTime()-dt2.getTime())/(60*1000);
        } catch (Exception e) {
            return 0;
        }

    }

    /*
    * @param d1    	开始日期
    * 				格式yyyy-MM-dd
    * @param d2    	结束日期.
    *
    * @return      按月分隔的list，list里面每个月一个map,第一天begindate，最后一天enddate
    */
    public static List<HashMap> getDateBetween (String d1, String d2) {
        ArrayList<HashMap> list = new ArrayList<HashMap>();
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(sdf.parse(d1));
            cal2.setTime(sdf.parse(d2));

            int monthnum =(cal2.get(Calendar.YEAR)- cal1.get(Calendar.YEAR))*12 + cal2.get(Calendar.MONTH)- cal1.get(Calendar.MONTH);
            for(int i=0;i<monthnum;i++){
                HashMap<String,Object> map =  new HashMap<String,Object>();
                map.put("begindate",sdf.format(cal1.getTime()));
                cal1.add(Calendar.DATE,cal1.getActualMaximum(Calendar.DATE)-cal1.get(Calendar.DATE));
                map.put("enddate",sdf.format(cal1.getTime()));
                list.add(map);
                cal1.add(Calendar.MONTH,1);
                cal1.add(Calendar.DATE,1-cal1.get(Calendar.DATE));
            }
            HashMap<String,Object> map =  new HashMap<String,Object>();
            map.put("begindate",sdf.format(cal1.getTime()));
            map.put("enddate",d2);
            list.add(map);
        } catch (Exception e) {
            return list;
        }
        return list;
    }
    /*
    * 两个时间段得相交的天数
    * 				格式yyyy-MM-dd
    *
    *
    * @return      天数
    */
    public static long getDays(String b1,String e1,String b2,String e2){
        long ret = 0;
        String begindate = "";
        String enddate = "";
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Calendar begin1 = Calendar.getInstance();
            Calendar end1 = Calendar.getInstance();
            Calendar begin2 = Calendar.getInstance();
            Calendar end2 = Calendar.getInstance();
            begin1.setTime(sdf.parse(b1));
            end1.setTime(sdf.parse(e1));
            begin2.setTime(sdf.parse(b2));
            end2.setTime(sdf.parse(e2));
            //时间段不相交
            if ((begin2.getTime().getTime()>end1.getTime().getTime() && end2.getTime().getTime()>end1.getTime().getTime()) ||
                    (begin2.getTime().getTime()<begin1.getTime().getTime() && end2.getTime().getTime()<begin1.getTime().getTime())) {
                //System.out.println("b"+ret);
                return ret;
            }

            if (begin2.getTime().getTime() >=  begin1.getTime().getTime()){
                begindate = sdf.format(begin2.getTime());
            }else{
                begindate = sdf.format(begin1.getTime());
            }
            if (end2.getTime().getTime()>= end1.getTime().getTime()){
                enddate = sdf.format(end1.getTime());
            }else{
                enddate = sdf.format(end2.getTime());
            }

            if (!begindate.equals("") && !enddate.equals("")){
                ret = getDisDays(begindate,enddate);
            }
        } catch (Exception e) {

        }
        //System.out.println("e"+ret);
        return ret;
    }
    /**
     * 比较2个格式为yyyy-MM-dd的日期<br>
     * 若d1小于d2返回true<br>
     * d1=2007-10-01,d2=2007-10-15,则返回true
     * @return
     */
    public static boolean after(String d1,String d2){
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Date dt1=sdf.parse(d1);
            Date dt2=sdf.parse(d2);
            return dt1.getTime() < dt2.getTime();
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 比较2个日期
     *
     * @return
     */
    public static boolean afterAndEqual(String d1,String d2){
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Date dt1=sdf.parse(d1);
            Date dt2=sdf.parse(d2);
            return dt1.getTime() <= dt2.getTime();
        } catch (Exception e) {
            return false;
        }
    }

    /*
    * 移动日期
    * @param date 原日期
    * @param len 移动量
    * @return 移动后日期
    */
    public static String dayMove(String date,int len){
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            cal.add(Calendar.DATE, len);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return date;
        }
    }

    public static String getCurrentMonth(){
        Calendar today=Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String curmonth=sdf.format(today.getTime());
        return curmonth;
    }

    /*
    * 移动月份
    * @param date 原日期
    * @param len 移动量
    * @return 移动后日期
    */
    public static String monthMove(String date,int len){
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM");
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            cal.add(Calendar.MONTH, len);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return date;
        }
    }

    /*
    * 截取2个时间段相交的时间段
    *
    * @return  String[] = {array[0]=begindate ,array[1]=enddate}
    * 不相交    array[0]=""
    *
    */
    public static String[] getBetweenDate(String b1,String e1,String b2,String e2){
        String[] date = new String[2];
        String begindate = "";
        String enddate = "";
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Calendar begin1 = Calendar.getInstance();
            Calendar end1 = Calendar.getInstance();
            Calendar begin2 = Calendar.getInstance();
            Calendar end2 = Calendar.getInstance();
            begin1.setTime(sdf.parse(b1));
            end1.setTime(sdf.parse(e1));
            begin2.setTime(sdf.parse(b2));
            end2.setTime(sdf.parse(e2));
            if ((begin2.getTime().getTime()>=end1.getTime().getTime() && end2.getTime().getTime()>=end1.getTime().getTime()) ||
                    (begin2.getTime().getTime()<=begin1.getTime().getTime() && end2.getTime().getTime()<=begin1.getTime().getTime())) {
                date[0] = "";
                return date;
            }

            if (begin2.getTime().getTime() >=  begin1.getTime().getTime()){
                begindate = sdf.format(begin2.getTime());
            }else{
                begindate = sdf.format(begin1.getTime());
            }
            if (end2.getTime().getTime()>= end1.getTime().getTime()){
                enddate = sdf.format(end1.getTime());
            }else{
                enddate = sdf.format(end2.getTime());
            }

            if (!begindate.equals("") && !enddate.equals("")){
                date[0] = begindate;
                date[1] = enddate;
            }
        } catch (Exception e) {

        }
        return date;
    }


    public static Date date = null;

    public static DateFormat dateFormat = null;

    public static Calendar calendar = null;

    /**
     * 功能描述：格式化日期
     *
     * @param dateStr
     *            String 字符型日期
     * @param format
     *            String 格式
     * @return Date 日期
     */
    public static Date parseDate(String dateStr, String format) {
        try {
            dateFormat = new SimpleDateFormat(format);
            String dt = dateStr.replaceAll("-", "/");
            if ((!dt.equals("")) && (dt.length() < format.length())) {
                dt += format.substring(dt.length()).replaceAll("[YyMmDdHhSs]",
                        "0");
            }
            date = (Date) dateFormat.parse(dt);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * 功能描述：格式化日期
     *
     * @param dateStr
     *            String 字符型日期：YYYY-MM-DD 格式
     * @return Date
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, "yyyy/MM/dd");
    }

    /**
     * 功能描述：格式化输出日期
     *
     * @param date
     *            Date 日期
     * @param format
     *            String 格式
     * @return 返回字符型日期
     */
    public static String format(Date date, String format) {
        String result = "";
        try {
            if (date != null) {
                dateFormat = new SimpleDateFormat(format);
                result = dateFormat.format(date);
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 功能描述：
     *
     * @param date
     *            Date 日期
     * @return
     */
    public static String format(Date date) {
        return format(date, "yyyy/MM/dd");
    }

    /**
     * 功能描述：返回年份
     *
     * @param date
     *            Date 日期
     * @return 返回年份
     */
    public static int getYear(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 功能描述：返回月份
     *
     * @param date
     *            Date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 功能描述：返回日份
     *
     * @param date
     *            Date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 功能描述：返回小时
     *
     * @param date
     *            日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 功能描述：返回分钟
     *
     * @param date
     *            日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date
     *            Date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 功能描述：返回毫秒
     *
     * @param date
     *            日期
     * @return 返回毫秒
     */
    public static long getMillis(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

//    /**
//     * 功能描述：返回字符型日期
//     *
//     * @param date
//     *            日期
//     * @return 返回字符型日期 yyyy/MM/dd 格式
//     */
//    public static String getDate(Date date) {
//        return format(date, "yyyy/MM/dd");
//    }

    /**
     * 功能描述：返回字符型时间
     *
     * @param date
     *            Date 日期
     * @return 返回字符型时间 HH:mm:ss 格式
     */
    public static String getTime(Date date) {
        return format(date, "HH:mm:ss");
    }

    /**
     * 功能描述：返回字符型日期时间
     *
     * @param date
     *            Date 日期
     * @return 返回字符型日期时间 yyyy/MM/dd HH:mm:ss 格式
     */
    public static String getDateTime(Date date) {
        return format(date, "yyyy/MM/dd HH:mm:ss");
    }

    /**
     * 功能描述：日期相加
     *
     * @param date
     *            Date 日期
     * @param day
     *            int 天数
     * @return 返回相加后的日期
     */
    public static Date addDate(Date date, int day) {
        calendar = Calendar.getInstance();
        long millis = getMillis(date) + ((long) day) * 24 * 3600 * 1000;
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    /**
     * 功能描述：日期相减
     *
     * @param date
     *            Date 日期
     * @param date1
     *            Date 日期
     * @return 返回相减后的日期
     */
    public static int diffDate(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
    }

    /**
     * 功能描述：取得指定月份的第一天
     *
     * @param strdate
     *            String 字符型日期
     * @return String yyyy-MM-dd 格式
     */
    public static String getMonthBegin(String strdate) {
        date = parseDate(strdate);
        return format(date, "yyyy-MM") + "-01";
    }

    /**
     * 功能描述：取得指定月份的最后一天
     *
     * @param strdate
     *            String 字符型日期
     * @return String 日期字符串 yyyy-MM-dd格式
     */
    public static String getMonthEnd(String strdate) {
        date = parseDate(getMonthBegin(strdate));
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return formatDate(calendar.getTime());
    }

    /**
     * 功能描述：常用的格式化日期
     *
     * @param date
     *            Date 日期
     * @return String 日期字符串 yyyy-MM-dd格式
     */
    public static String formatDate(Date date) {
        return formatDateByFormat(date, "yyyy-MM-dd");
    }

    /**
     * 功能描述：以指定的格式来格式化日期
     *
     * @param date
     *            Date 日期
     * @param format
     *            String 格式
     * @return String 日期字符串
     */
    public static String formatDateByFormat(Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
