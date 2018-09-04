package com.wondertek.mam.util.network;

import java.io.Serializable;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetworkUtil implements Serializable {
	private static final long serialVersionUID = -7521577391890905776L;
	private final static Log log = LogFactory.getLog(NetworkUtil.class);
	static Enumeration<NetworkInterface> nets = null;
	static {
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否是IP地址
	 * @param str
	 * @return
	 */
	public static boolean isIPAdress(String str) {
		if (StringUtils.isBlank(str))
			return false;
		Pattern pattern = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 检测发起请求IP与应用的IP是否在同网段
	 * 
	 * @param reqIp
	 * @return
	 */
	public static boolean isLocalRequest(String reqIp) {
		boolean isTrue = false;
		if (isInnerIp(reqIp))
			return true;
		try {
			if (null == nets)
				nets = NetworkInterface.getNetworkInterfaces();
			while (nets.hasMoreElements()) {
				NetworkInterface inf = nets.nextElement();
				if (null == inf)
					continue;
				String localIp = getInfIP(inf);
				log.debug("get  local IP: " + localIp);
				if (StringUtils.isBlank(localIp))
					continue;
				isTrue = isSameNet(reqIp, localIp, "255.255.0.0");
				log.debug("chk request IP: " + reqIp + "  local IP: " + localIp + "  with mask 255.255.0.0, result: " + isTrue);
				if (isTrue)
					return isTrue;
			}
		} catch (Exception e) {
		}
		return isTrue;
	}

	/**
	 * 获取网卡IP地址
	 * 
	 * @param netInterface
	 * @return
	 */
	public static String getInfIP(NetworkInterface netInterface) {
		String ip = "";
		if (netInterface != null) {
			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress inetAddress = addresses.nextElement();
				if (inetAddress instanceof Inet6Address) {
					continue;
				} else {
					ip = inetAddress.getHostAddress();
				}
			}
		}
		return ip;
	}

	/**
	 * 检测两个IP是在同一网段
	 * 
	 * @param remotIp
	 * @param localIp
	 * @return
	 */
	public static boolean isSameNet(String remotIp, String localIp, String mask) {
		if (StringUtils.isBlank(remotIp) || remotIp.equalsIgnoreCase("localhost"))
			remotIp = "127.0.0.1";

		if (StringUtils.isBlank(mask))
			mask = "255.255.0.0";
		long addr[] = { 0, 0 };
		long maskNum = 0;
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(remotIp);
		for (int j = 0; j < 4; j++) {
			m.find();
			addr[0] <<= 8;
			addr[0] += Integer.parseInt(m.group());
		}
		m = p.matcher(localIp);
		for (int j = 0; j < 4; j++) {
			m.find();
			addr[1] <<= 8;
			addr[1] += Integer.parseInt(m.group());
		}
		m = p.matcher(mask);
		for (int j = 0; j < 4; j++) {
			m.find();
			maskNum <<= 8;
			maskNum += Integer.parseInt(m.group());
		}
		return (addr[0] & maskNum) == (addr[1] & maskNum);
	}

	/**
	 * 判断是否是内网IP 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
	 * 192.168.0.0-192.168.255.255
	 */
	public static boolean isInnerIp(String ip) {
		boolean isInner = false;
		isInner = (isSameNet(ip, "10.0.0.1", "255.0.0.0") || isSameNet(ip, "172.0.0.1", "255.0.0.0") || isSameNet(ip, "192.168.0.1", "255.0.0.0"));
		return isInner;

	}

	/**
	 * 获取服务器主机名
	 * @return
	 */
	public static String getHostName() {
		String hs = "";
		try {
			hs = (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException uhe) {
			String host = uhe.getMessage();
			if (host != null) {
				int colon = host.indexOf(':');
				if (colon > 0) {
					hs = host.substring(0, colon);
				}
			}
			hs = "UnknownHost";
		} catch (Exception e) {
			hs = "UnknownHost";
		}
		return hs;
	}
}
