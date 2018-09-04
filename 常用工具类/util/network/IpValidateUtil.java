package com.wondertek.mam.util.network;

public class IpValidateUtil {

	
    public static  boolean isModifyInnerUrl(String reqHost){
    	boolean isModify=false;
    	if(reqHost!=null&&!"".equals(reqHost)){
    		String sUrl="";
    		if(reqHost.indexOf(":")>0){
    			sUrl=reqHost.substring(0, reqHost.indexOf(":"));
    		}else sUrl=reqHost;
    		isModify=isInnerIP(sUrl);
    	}
    	return isModify;
    }
	
	/*
	 * 判断是否是内网IP 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
	 * 192.168.0.0-192.168.255.255
	 */
	public static boolean isInnerIP(String ipAddress) {
		boolean isInnerIp = false;
		if(ipAddress != null && !"".equals(ipAddress)){
			if("localhost".equalsIgnoreCase(ipAddress.trim())){
				ipAddress = "127.0.0.1";
			}
			long ipNum = getIpNum(ipAddress);
			long aBegin = getIpNum("10.0.0.0");
			long aEnd = getIpNum("10.255.255.255");
			long bBegin = getIpNum("172.16.0.0");
			long bEnd = getIpNum("172.31.255.255");
			long cBegin = getIpNum("192.168.0.0");
			long cEnd = getIpNum("192.168.255.255");
			isInnerIp = isInner(ipNum, aBegin, aEnd)
					|| isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
					|| ipAddress.equals("127.0.0.1");
		}
		return isInnerIp;
	}

	/* 获取IP数 */
	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}

}
