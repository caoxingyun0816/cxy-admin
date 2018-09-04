package com.wondertek.mam.util.others;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchStringUtil {
	public static final String STR_COMMA = ",";
	public static final String STR_COLON = ":";
	public static final String STR_SPACE = " ";

	public static List<String> splitByComma(String str) {
		if (str == null || "".equals(str))
			return null;
		List<String> strList = new ArrayList<String>();
		String[] strs = str.split(STR_COMMA);
		for (int i = 0; i < strs.length; i++) {
			if (!"".equals(strs[i]))
				strList.add(strs[i]);
		}
		return strList.isEmpty() ? null : strList;
	}
	
	public static List<String> splitBySpace(String str){
		if (str == null || "".equals(str))
			return null;
		List<String> strList = new ArrayList<String>();
		String[] strs = str.split(STR_SPACE);
		for (int i = 0; i < strs.length; i++) {
			if (!"".equals(strs[i]))
				strList.add(strs[i].trim());
		}
		return strList.isEmpty() ? null : strList;
	}
	
	public static String[] splitBySpaceToArray(String str){
		if (str == null || "".equals(str))
			return null;
		return str.split(STR_SPACE);
	}
	
	public static HashMap<String, String> splitBySpaceToMap(String str){
		if (str == null || "".equals(str))
			return null;
		HashMap<String, String> strMap = new HashMap<String, String>();
		String[] strs = str.split(STR_SPACE);
		if(strs != null && strs.length == 2)
			strMap.put(strs[0], strs[1]);
		return strMap.isEmpty() ? null : strMap;
	}
	
	public static HashMap<String, String> splitBySpace(String str, HashMap<String, String> strMap){
		if (str == null || "".equals(str))
			return null;
		String[] strList = str.split(STR_SPACE);
		if (strList.length != 2)
			return null;
		strMap.put(strList[0].trim(), strList[1].trim());
		return strMap;
	}

	public static HashMap<String, String> splitByColon(String str,
			HashMap<String, String> strMap) {
		if (str == null || "".equals(str))
			return null;
		String[] strList = str.split(STR_COLON);
		if (strList.length != 2)
			return null;
		strMap.put(strList[0].trim(), strList[1].trim());
		return strMap;
	}
	
	public static HashMap<String, String> splitToMap(String str) {
		List<String> strList = splitByComma(str);
		if (strList == null)
			return null;
		HashMap<String, String> strMap = new HashMap<String, String>();
		for (int i = 0; i < strList.size(); i++) {
			if(strList.get(i).contains(STR_COLON))
				splitByColon(strList.get(i), strMap);
			if(strList.get(i).contains(STR_SPACE) && !strList.get(i).contains(STR_COLON))
				splitBySpace(strList.get(i), strMap);
		}
		return (strMap != null && !strMap.isEmpty()) ? strMap : null;
	}

	public static int parseInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
