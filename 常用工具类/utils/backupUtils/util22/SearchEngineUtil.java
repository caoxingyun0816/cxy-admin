/*
package com.wondertek.mam.util.backupUtils.util22;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.wondertek.mobilevideo.search.core.SearchConstants;
import com.wondertek.mobilevideo.search.core.model.BlackWord;

public class SearchEngineUtil {
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

	public static List<String> splitBySpace(String str) {
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

	public static HashMap<String, String> splitBySpace(String str, HashMap<String, String> strMap) {
		if (str == null || "".equals(str))
			return null;
		String[] strList = str.split(STR_SPACE);
		if (strList.length != 2)
			return null;
		strMap.put(strList[0].trim(), strList[1].trim());
		return strMap;
	}

	public static HashMap<String, String> splitByColon(String str, HashMap<String, String> strMap) {
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
			if (strList.get(i).contains(STR_COLON))
				splitByColon(strList.get(i), strMap);
			if (strList.get(i).contains(STR_SPACE) && !strList.get(i).contains(STR_COLON))
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

	public static String keywordHandle(String k) {
		Set<String> set = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		try {
			Analyzer analyzer = new IKAnalyzer(false);
			TokenStream tokenStream = analyzer.reusableTokenStream("text", new StringReader(k));
			CharTermAttribute term = (CharTermAttribute) tokenStream.getAttribute(CharTermAttribute.class);
			while (tokenStream.incrementToken()) {
				set.add(term.toString());
			}
			if (!set.isEmpty()) {
				for (String s : set) {
					sb.append(s);
					sb.append(" ");
				}
			} else {
				sb.append(k);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString().trim();

	}

	public static Map sortByValue(Map map, final boolean reverse) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {

			public int compare(Object o1, Object o2) {
				if (reverse) {
					return -((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
				}
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static boolean containsBlackWord(String result) {
		if (SearchConstants.blackWordList != null && !SearchConstants.blackWordList.isEmpty()) {
			for (BlackWord blackWord : SearchConstants.blackWordList) {
				if (result.contains(blackWord.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
*/
