package com.wondertek.mam.util.others;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**

 * <p>Calendar calendar = Calendar.getInstance();</p>
 * <p>java.util.Date date = calendar.getTime();</p>
 * <p>Copyright: Copyright (c) 2002.6.24</p>
 * <p>Company: wondertek</p>
 * @author ice
 * @version 1.0
 */

public class TimeTool {
    private static final String errPreFix = "com.wondertek.fsy.contract.util.TimeTool\r\n";

    private SimpleDateFormat dateTimeFormat = null;

    private SimpleDateFormat dateFormat = null;

    private SimpleDateFormat noSecondFormat = null;
    
    private SimpleDateFormat yearMonthFormat = null;

    private SimpleDateFormat timeFormat = null;

    public static ParsePosition parsePos = null;

    private static final int TYPE_DATE_TIME = 1;

    private static final int TYPE_DATE = 2;

    private static final int TYPE_TIME = 3;

    private static final int TYPE_NOSECONF_TIME = 4;
    
    private static final int TYPE_YEAR_MONTH = 5;
    
    private static final Long dayToMil = 86400000L;
    private static final Long hourToMil = 3600000L;
    private static final Long minuteToMil = 60000L;
    private static final Long secondToMil = 1000L;
    
    //�����������ҳ��������ʱ��(10����)
//    public static long settle_Max_Time = MyConfig.settleMaxTime;


    private TimeTool(int type) {
        if (type == TYPE_DATE_TIME) {
            dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (type == TYPE_DATE) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else if (type == TYPE_TIME) {
            timeFormat = new SimpleDateFormat("HH:mm:ss");
        } else if (type == TYPE_NOSECONF_TIME) {
        	noSecondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");   
        } else if (type == TYPE_YEAR_MONTH) {
        	yearMonthFormat = new SimpleDateFormat("yyyyMM");   
        } else {
            throw new IllegalArgumentException(errPreFix + "type error:type = "
                                               + type);
        }
        parsePos = new ParsePosition(0);
    }


    public static String getYearMonthStr(String year, String month) {
        int intMonth = Integer.parseInt(month) + 1;
        return year + "-" + (intMonth < 10 ? "0" + intMonth : "" + intMonth);
    }

    public static Calendar getCalenarDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public static boolean before(int[] yearMonth01, int[] yearMonth02) {
        return yearMonth01[0] < yearMonth02[0]
            || yearMonth01[0] == yearMonth02[0] && yearMonth01[1] < yearMonth02[1];
    }

    public static boolean after(int[] yearMonth01, int[] yearMonth02) {
        return yearMonth01[0] > yearMonth02[0]
            || yearMonth01[0] == yearMonth02[0] && yearMonth01[1] > yearMonth02[1];
    }

    public static boolean equals(int[] yearMonth01, int[] yearMonth02) {
        return yearMonth01[0] == yearMonth02[0] && yearMonth01[1] == yearMonth02[1];
    }


    public static boolean equalsYearMonthDay(Calendar calendar01, Calendar calendar02) {
        return calendar01.get(Calendar.YEAR) == calendar02.get(Calendar.YEAR)
            && calendar01.get(Calendar.MONTH) == calendar02.get(Calendar.MONTH)
            && calendar01.get(Calendar.DAY_OF_MONTH) == calendar02.get(Calendar.DAY_OF_MONTH);
    }


    public static String getWeekShow(int week) {
        if (week < 1 || week > 7) {
            return null;
        }
        String[] WEEK = {
            "������", "����һ", "���ڶ�", "������", "������", "������", "������"
        };
        return WEEK[week - 1];
    }

    public static void addMonth(int[] yearMonth, int amount) {
        int year = yearMonth[0];
        int month = yearMonth[1];
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException(
                errPreFix +"addMonth month is invaid:month = " + month);
        }
        year += amount / 12;
        month += amount % 12;
        if (month < 0) {
            month += 12;
            year--;
        } else if (month > 11) {
            month -= 12;
            year++;
        }
        yearMonth[0] = year;
        yearMonth[1] = month;
    }


    public static int diffOfDay(Calendar calendar1, Calendar calendar2) {
        long time = diffOfMillis(calendar1, calendar2);
        return (int)(time / (1000 * 60 * 60 * 24));
    }


    public static int diffOfField(Calendar calendar1, Calendar calendar2, int field) {
        return Math.abs(calendar1.get(field) - calendar2.get(field));
    }


    public static long diffOfMillis(Calendar calendar1, Calendar calendar2) {
        return Math.abs(calendar1.getTimeInMillis() - calendar2.getTimeInMillis());
    }

  
    public static String longToStrDateTime(long time) {
        return dateToStrDateTime(new Date(time));
    }


    public static Calendar longToCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public static long strDateTimeToLong(String str) {
        return strDateTimeToDate(str).getTime();
    }

    public static long calendarToLong(Calendar calendar) {
        if (calendar == null) {
            return -1;
        }
        return calendar.getTimeInMillis();
    }


    public static String getDateTime() {
        return dateToStrDateTime(new Date());
    }


    public static String getDate() {
        return dateToStrDate(new Date());
    }


    public static String getTime() {
        return dateToStrTime(new Date());
    }

    public static Date strDateTimeToDate(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 10) {
            str += " 00:00:00";
        }

        Date date = new TimeTool(TYPE_DATE_TIME).dateTimeFormat.parse(str, parsePos);
        return date;
    }
    
    public static Date strDateNoSecondTimeToDate(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 10) {
            str += " 00:00:00";
        }

        Date date = new TimeTool(TYPE_NOSECONF_TIME).noSecondFormat.parse(str, parsePos);
        return date;
    }
    

    public static Calendar strDateTimeToCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        Date date = strDateTimeToDate(str);
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        return calendar;
    }
    
    public static Calendar strDateNoSecondTimeToCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        Date date = strDateNoSecondTimeToDate(str);
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        return calendar;
    }


    public static Calendar strToCalendar(String str) {
        return strDateTimeToCalendar(str);
    }


    public static Date strDateToDate(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 7) {
            str += "-01 00:00:00";
        }
        
        Date date = new TimeTool(TYPE_DATE).dateFormat.parse(str, parsePos);
        return date;
    }


    public static Calendar strDateToCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        Date date = strDateToDate(str);
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        return calendar;
    }

    public static String dateToStrDateTime(Date date) {
        if (date == null) {
            return null;
        }
        String str = new TimeTool(TYPE_DATE_TIME).dateTimeFormat.format(date);
        return str;
    }
    
    public static String dateToStrDateNoSecondTime(Date date) {
        if (date == null) {
            return null;
        }
        String str = new TimeTool(TYPE_NOSECONF_TIME).noSecondFormat.format(date);
        return str;
    }
    
    public static String dateToStrYearMonth(Date date) {
        if (date == null) {
            return null;
        }
        String str = new TimeTool(TYPE_YEAR_MONTH).yearMonthFormat.format(date);
        return str;
    }
    
    public static String calendarToStrDateTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrDateTime(calendar.getTime());
    }
    
    public static String calendarToStrYearMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrYearMonth(calendar.getTime());
    }
    
    public static String calendarToStrDateNoSecondTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrDateNoSecondTime(calendar.getTime());
    }


    public static String dateToStrDate(Date date) {
        if (date == null) {
            return null;
        }
        String str = new TimeTool(TYPE_DATE).dateFormat.format(date);
        return str;
    }

    public static String calendarToStrDate(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrDate(calendar.getTime());
    }

 
    public static String dateToStrTime(Date date) {
        if (date == null) {
            return null;
        }
        String str = new TimeTool(TYPE_TIME).timeFormat.format(date);
        return str;
    }


    public static String calendarToStrTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrTime(calendar.getTime());
    }

    public static String calendarToStrDateCn(Calendar cal){
    	if(cal == null){
    		return null;
    	}
    	return cal.get(Calendar.YEAR)+"��"+(cal.get(Calendar.MONTH)+1)+"��"+cal.get(Calendar.DATE)+"��";
    }

	public static String getCNYearMonth(){
		String date = "";
		Calendar cal = Calendar.getInstance();
		String year = ""+cal.get(Calendar.YEAR);
		String month = ""+(cal.get(Calendar.MONTH)+1);
		String year1 = year.substring(0, 1);
		String year2 = year.substring(1, 2);
		String year3 = year.substring(2, 3);
		String year4 = year.substring(3, 4);
		String month1 = month.substring(0, 1);
		String month2 = "";
		if(month.length() > 1)
			month2 = month.substring(1, 2);
		date = numToCNnum(year1)+" "+numToCNnum(year2)+" "+numToCNnum(year3)+" "+numToCNnum(year4)+" �� "+numToCNnum(month1);
		if(month2.length() > 0)
			date += " "+numToCNnum(month2)+" ��";
		else
			date += " ��";
		return date;
	}

	private static String numToCNnum(String str){
		if(str == null || str.length() == 0 || !StringUtils.isNumeric(str))
			return "";
		else{
			char[] num = {'0','1','2','3','4','5','6','7','8','9'};
			String[] cnnum = {"��","һ","��","��","��","��","��","��","��","��"};
			String result = "";
			for (int i = 0; i < str.length(); i++) {
				char temp = str.charAt(i);
				int z = 0;
				for (int j = 0; j < num.length; j++) {
					if(temp == num[j]){
						z = j;
						break;
					}
				}
				result = cnnum[z];
			}
			return result;
		}
	}
	
	public static String getCnTimeByTime(Calendar date1,Calendar date2){
		String time = "";
		if(date1 == null || date2 == null){
			return time;
		}
		Long dif = date2.getTimeInMillis() - date1.getTimeInMillis();
		long ms = 0L;
		long s = 0L;
		long m = 0L;
		long h = 0L;
		long d = 0L;
		if(dif > 0){
			if(dif >= (TimeTool.dayToMil)){	//大于�??�??
				d = dif / TimeTool.dayToMil;
				dif = dif % TimeTool.dayToMil;
			}
			if(dif >= (TimeTool.hourToMil)){	//大于�??个小�??
				h = dif / TimeTool.hourToMil;
				dif = dif % TimeTool.hourToMil;
			}
			if(dif >= (TimeTool.minuteToMil)){	//大于�??分钟
				m = dif / TimeTool.minuteToMil;
				dif = dif % TimeTool.minuteToMil;
			}
			if(dif >= (TimeTool.secondToMil)){	//大于�??�??
				s = dif / TimeTool.secondToMil;
				dif = dif % TimeTool.secondToMil;
			}
			if(dif > 0){
				ms = dif;
			}
		}
		
		time = ((d != 0)?d+"�??":"")+((h != 0)?h+"小时":"")+
			   ((m != 0)?m+"分钟":"")+((s != 0)?s+"�??":"")+
			   ((ms != 0)?ms+"毫秒":"");
		if("".equals(time)){
			time = "0毫秒";
		}
		return time;
	}
 }
