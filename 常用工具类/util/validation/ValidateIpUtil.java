package com.wondertek.mam.util.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class ValidateIpUtil {

	private static Logger log = Logger.getLogger(ValidateIpUtil.class);

	public static Map<String, Object> ipSegmentCache = new HashMap<String, Object>();

	/*
	 * @author xiangjiang 解析IP文件 并将文件存入缓存中，在IP段文件中，第一部分为起始Ip,第二部分为终止Ip,第三段为掩码
	 * 目前掩码没有用到，暂时保留
	 */
	public static void parseIpFile() {
		log.info("==============start-to-parse-ipFile===========");
		// 起始IP 与止IP 映射
		Map<String, Set<String>> startEndMap = new HashMap<String, Set<String>>();
		// key-value:IP[0]-<IP[1]-IP起始列表>
		Map<String, Map<String, Set<String>>> finalMap = new HashMap<String, Map<String, Set<String>>>();
		/*String ipFilePath = PortalConstants.conf.getProperty("ipSegmentFilePath");
		File file = new File(ipFilePath);*/
		//以上两行注释为了解决代码报错的问题，用下面一行代码代替的
		File file = new File("");
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String tempString = "";
			while ((tempString = bf.readLine()) != null) {
				String[] segMent = tempString.split("\\|");

				// 起止IP映射
				if (startEndMap.get(segMent[0]) == null || startEndMap.get(segMent[0]).size() == 0) {
					Set<String> tempSet = new HashSet<String>();
					tempSet.add(segMent[1]);
					startEndMap.put(segMent[0], tempSet);
				} else {
					startEndMap.get(segMent[0]).add(segMent[1]);
				}
				int a = Integer.valueOf(segMent[0].split("\\.")[1]);
				int b = Integer.valueOf(segMent[1].split("\\.")[1]);
				String[] ipArray = segMent[0].split("\\.");
				Map<String, Set<String>> tempMap = finalMap.get(ipArray[0]);
				if (tempMap == null || tempMap.size() == 0) {
					tempMap = new HashMap<String, Set<String>>();
				}

				// 例如 180.70.0.0--180.73.255.255，则需要将每一个ip[0]存入进去，如70,71,72,73
				if (a != b) {
					for (int i = a; i <= b; i++) {
						if (tempMap.get(String.valueOf(i)) == null) {
							Set<String> set = new HashSet<String>();
							set.add(segMent[0]);
							tempMap.put(String.valueOf(i), set);
						} else {
							tempMap.get(String.valueOf(i)).add(segMent[0]);
						}
					}
				} else {
					if (tempMap.get(ipArray[1]) == null) {
						Set<String> set = new HashSet<String>();
						set.add(segMent[0]);
						tempMap.put(ipArray[1], set);
					} else {
						tempMap.get(ipArray[1]).add(segMent[0]);
					}
				}
				// 将IP[0] IP[1]映射起来
				finalMap.put(ipArray[0], tempMap);
			}

			ipSegmentCache.put("ipStartEndMap", startEndMap);
			ipSegmentCache.put("ipFinalMap", finalMap);

			bf.close();
		} catch (Exception e) {
			log.error("parse_ip_file_error", e);
		}
		log.info("==============parse-ipFile-end===========");
	}

	/*
	 * 判断是否为国内Ip，如果是 返回 true
	 */
	@SuppressWarnings("unchecked")
	public static boolean isDomainIp(String ipAddress) {
		
		//关闭IP过滤功能
	/*	if("false".equals(PortalConstants.conf.getProperty("if_open_forgein_ip_filter"))){
			return true;
		}*/
		Map<String, Map<String, Set<String>>> finalMap = (Map<String, Map<String, Set<String>>>) ipSegmentCache
				.get("ipFinalMap");
		Map<String, Set<String>> startEndMap = (Map<String, Set<String>>) ipSegmentCache.get("ipStartEndMap");
		if (finalMap == null || finalMap.size() == 0 || startEndMap == null || startEndMap.size() == 0) {
			parseIpFile();
			finalMap = (Map<String, Map<String, Set<String>>>) ipSegmentCache.get("ipFinalMap");
			startEndMap = (Map<String, Set<String>>) ipSegmentCache.get("ipStartEndMap");
		}
		String[] inputIpArray = ipAddress.split("\\.");
		if (finalMap == null) {
			log.error("finalMap is null");
			return false;
		}
		Map<String, Set<String>> hhMap = finalMap.get(inputIpArray[0]);
		if (hhMap == null) {
			log.error(ipAddress + ":是国外网段");
			return false;
		}

		Set<String> returnSet = hhMap.get(inputIpArray[1]);
		boolean flag = false;
		if (returnSet != null && returnSet.size() > 0) {
			for (String str : returnSet) {
				long inputIp = getIpNum(inputIpArray);
				long startIp = getIpNum(str.split("\\."));
				if (inputIp > startIp) {
					Set<String> endIpSet = startEndMap.get(str);
					for (String temString : endIpSet) {
						long endIp = getIpNum(temString.split("\\."));
						if (endIp >= inputIp) {
							flag = true;
							log.info(ipAddress + ":是国内网段");
							return true;
						}
					}
				} else if (inputIp == startIp) {
					flag = true;
					log.info(ipAddress + ":是国内网段");
					return true;
				}
			}
		}
		if (flag) {
			log.info(ipAddress + ":是国内网段");
			return true;
		} else {
			log.info(ipAddress + ":是国外网段");
			return false;
		}
	}

	private static long getIpNum(String[] ip) {
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);
		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static long getIpNum(int[] ip) {
		long a = ip[0];
		long b = ip[1];
		long c = ip[2];
		long d = ip[3];
		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}
}
