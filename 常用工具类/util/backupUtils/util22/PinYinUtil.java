/*
package com.wondertek.mam.util.backupUtils.util22;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
	private static HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();

	static {
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
	}

	public static String converterToFirstSpell(String src) {
		String result = "";
		try {
			Set<String> set = getPinyin(src, false, true);
			result = makeStringByStringSet(set);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String converterToSpell(String src, boolean whitespace) {
		String result = "";
		try {
			Set<String> set = getPinyin(src, whitespace, false);
			result = makeStringByStringSet(set);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	private static String makeStringByStringSet(Set<String> stringSet) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (String s : stringSet) {
			if (i == stringSet.size() - 1) {
				str.append(s);
			} else {
				str.append(s + ",");
			}
			i++;
		}
		return str.toString().toLowerCase();
	}

	public static Set<String> getPinyin(String src, boolean whiteSpace, boolean firstSpell) {
		if (src != null && !src.trim().equalsIgnoreCase("")) {
			char[] srcChar;
			srcChar = src.toCharArray();

			String[][] temp = new String[src.length()][];
			for (int i = 0; i < srcChar.length; i++) {
				char c = srcChar[i];
				// 是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
				if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
					try {
						String[] pys = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], defaultFormat);
						Set<String> pySet = new HashSet<String>();
						for (String py : pys) {
							pySet.add(py);
						}
						temp[i] = new String[pySet.size()];
						int k = 0;
						for (String py : pySet) {
							if (whiteSpace) {
								temp[i][k++] = " " + py + " ";
							} else {
								temp[i][k++] = py;
							}
						}
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				} else if (((int) c >= 65 && (int) c <= 90) || ((int) c >= 97 && (int) c <= 122) || ((int) c >= 48 && (int) c <= 57) || (int) c == 32) {
					// 字母，数字，空格保留
					temp[i] = new String[] { String.valueOf(srcChar[i]) };
				} else {
					temp[i] = new String[] { "" };
				}
				if (temp[i] == null || temp[i].length <= 0)
					continue;

				for (int k = 0; k < temp[i].length; k++) {
					if (firstSpell && temp[i][k].toCharArray().length > 0) {
						temp[i][k] = String.valueOf(temp[i][k].charAt(0));
					}
				}

			}

			Set<String> pinyinSet = new HashSet<String>();

			List<String[]> list = CompositeUtil.getResult(temp);
			for (String[] strings : list) {
				String py = "";
				for (String s : strings) {
					py += s;
				}
				// 避免拼音过长
				if (py.length() > 50) {
					py = py.substring(0, 50);
				}
				pinyinSet.add(py.replaceAll("\\s{2,}", " "));
			}

			return pinyinSet;
		}
		return null;
	}

	public static void main(String[] args) {

		String s = "他们真的只是闺蜜那么简单？还是表面";
		System.out.println(s.substring(0, 10));
		System.out.println(converterToSpell("他们真的只是闺蜜那么简单？还是表面", true));
		System.out.println(converterToFirstSpell("他们真的只是闺蜜那么简单？还是表面"));

	}
}*/
