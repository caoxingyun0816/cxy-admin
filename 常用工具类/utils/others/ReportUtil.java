package com.wondertek.mam.util.others;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import com.wondertek.mobilevideo.core.util.StringUtil;


public class ReportUtil {
	
	private static String HOSTIP;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	

	// 获得windows本机IP
	public static String getWindowsHostIP() 
	{
		if (!StringUtil.isNullStr(HOSTIP))
		{
			return HOSTIP;
		}
		try 
		{
			InetAddress addr = InetAddress.getLocalHost();
			HOSTIP = addr.getHostAddress().toString();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		return HOSTIP;
	}
	
	// 获得linux本机IP
	public static String getLinuxHostIP() 
	{
		String ip = "";
        try {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) 
            {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals("eth0")) 
                {
                    continue;
                } 
                else 
                {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) 
                    {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address)
                            continue;
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        }
        return ip;
	}
	
	/**
	 * 执行shell命令
	 * @param command shell命令
	 * @return 执行命令输出流
	 * @throws IOException 
	 */
	public static BufferedReader exceteShellOrder(String command) throws IOException
	{
		BufferedReader bufferedReader = null;
		Process pid = Runtime.getRuntime().exec(
				"sh ../webapps/oms-basic/wrap.sh " + command);
		if (null != pid) {
			bufferedReader = new BufferedReader(new InputStreamReader(
					pid.getInputStream()), 1024);
		}
		return bufferedReader;
	}
	
	/**
	 * 把String的类型的日期转换成为Calendar类型的日期，如不存在返回当前时间的Calendar类型
	 * 
	 * @param stringDate
	 *            字符串的日期类型
	 * @return Calendar的日期类型
	 */
	public static Calendar toCalendarDate(String stringDate) {
		Calendar calendarDate = Calendar.getInstance();
		if (null != stringDate) {
			String[] start = (stringDate.substring(0, 10)).split("-");
			calendarDate.set(Integer.parseInt(start[0]),
					Integer.parseInt(start[1]) - 1, Integer.parseInt(start[2]));
		}
		return calendarDate;
	}
	
	/**
	 * 创建文件的方法
	 * @param tempDir 创建文件的绝对路劲
	 * @param tmpFileName 创建文件的文件名
	 * @return 创建好的文件
	 * @throws IOException
	 */
	public static File createFile(String tempDir, String tmpFileName) throws IOException {
		File file = new File(tempDir);
		if(!file.exists())
		{
			file.mkdirs();
		}
		file = new File(tempDir+tmpFileName);
		return file;
	}
	
}
