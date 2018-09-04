package com.wondertek.mam.util.others;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ClientTimeUtil {

	//2000-01-01 00:00:00
	private static long baseTime=946656000000l;
	
	public static int toClientTime(Date date){
		return (int)((date.getTime()-baseTime)/1000);
	}
	
	public static Date fromClientTime(int time){
		return new Date(time*1000l+baseTime);
	}
	
	public static void main(String[] args) throws Exception{
		Date d=new Date(baseTime);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    System.out.println(format.format(d));
	    String msg="2000-01-01 00:00:00";
	    System.out.println(format.parse(msg).getTime());
	}
}
