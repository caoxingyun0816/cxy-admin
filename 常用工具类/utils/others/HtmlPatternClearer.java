package com.wondertek.mam.util.others;

import com.wondertek.mam.model.AssetImage;
import com.wondertek.mobilevideo.core.util.FileIO;
import com.wondertek.mobilevideo.core.util.JsonUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlPatternClearer {
	// 图片格式样式
	public static final Pattern IMG_P = Pattern.compile("<img[^<>]*src=[\\\\]*[\\'\\\"](\\S+[^\\\\])[\\\\]*[\\'\\\"][^<>]*>");
	public static final Pattern SPAN_P = Pattern.compile("<span\\s+style=[\\\\]*[\\'\\\"]([^\\'\\\"]*)[\\\\]*[\\'\\\"]\\s*>(.*?)</span>");
	public static final Pattern RGB_FUNC_P = Pattern.compile("rgb\\(([\\d|\\s]*), ([\\d|\\s]*), ([\\d|\\s]*)\\)");
	// public static final Pattern WDML_REG =
	// Pattern.compile("((</?[B|U]>)|(\\s))*\\\\n((</?[B|U]>)|(\\s))*");
	public static final Pattern WDML_P = Pattern.compile("((</?[B|U]>)|(\\s))*\\\\n((</?[B|U]>)|(\\s))*");
	// 空格
	public static final String SPACE_REGX = "((&#160;)|(&nbsp;)|(\\s)){1,2}";
	public static final String SPACE_REPLACE = " ";

	// 换行样式
	public static final String BR_REGX = "<br\\s*\\\\*\\/?>";
	public static final String BR_REPLACE = "\\\\n";
	public static final String BR_REPLACED = "\\n";

	// div匹配器
	public static final String DIV_REGX = "<div[^><]*>(.*?)<\\\\*/div>";

	// 加粗开始
	public static final String STRONG_BEGIN_REGX = "<strong[^><]*>";
	public static final String STRONG_BEGIN_REPLACE = "<B>";
	// 加粗结束
	public static final String STRONG_END_REGX = "<\\\\*/strong>";
	public static final String STRONG_END_REPLACE = "</B>";

	// 下滑线开始
	public static final String ULINE_BEGIN_REGX = "<u[^><]*>";
	public static final String ULINE_BEGIN_REPLACE = "<U>";
	// 下滑线结束
	public static final String ULINE_END_REGX = "<\\\\*/u>";
	public static final String ULINE_END_REPLACE = "</U>";

	// 清空html注释
	public static final String HTML_NOTES_REGX = "<!--(?s).*?-->";
	// 清空html标签
	public static final String HTML_LABEL_REGX = "<\\\\*/?[^/?(B)|(U)|(C)|(N)][^><]*>";

	// 左转义符
	public static final String LEFT_ESCAPE = "&lt;";
	public static final String LEFT_REPLACE = "<";
	// 右转义符
	public static final String RIGHT_ESCAPE = "&gt;";
	public static final String RIGHT_REPLACE = ">";

	// 转义符
	public static final String HTML_ESCAPE = "&amp;";
	public static final String HTML_ESCAPE_REPLACE = "&";

	public static Map<String, String> colorMap = new HashMap<String, String>();
	static {
		colorMap.put("lightpink", "ffb6c1");
		colorMap.put("pink", "ffc0cb");
		colorMap.put("crimson", "dc143c");
		colorMap.put("lavenderblush", "fff0f5");
		colorMap.put("palevioletred", "db7093");
		colorMap.put("hotpink", "ff69b4");
		colorMap.put("deeppink", "ff1493");
		colorMap.put("mediumvioletred", "c71585");
		colorMap.put("orchid", "da70d6");
		colorMap.put("thistle", "d8bfd8");
		colorMap.put("plum", "dda0dd");
		colorMap.put("violet", "ee82ee");
		colorMap.put("magenta", "ff00ff");
		colorMap.put("fuchsia", "ff00ff");
		colorMap.put("darkmagenta", "8b008b");
		colorMap.put("purple", "800080");
		colorMap.put("mediumorchid", "ba55d3");
		colorMap.put("darkvoilet", "9400d3");
		colorMap.put("darkorchid", "9932cc");
		colorMap.put("indigo", "4b0082");
		colorMap.put("blueviolet", "8a2be2");
		colorMap.put("mediumpurple", "9370db");
		colorMap.put("mediumslateblue", "7b68ee");
		colorMap.put("slateblue", "6a5acd");
		colorMap.put("darkslateblue", "483d8b");
		colorMap.put("lavender", "e6e6fa");
		colorMap.put("ghostwhite", "f8f8ff");
		colorMap.put("blue", "0000ff");
		colorMap.put("mediumblue", "0000cd");
		colorMap.put("midnightblue", "191970");
		colorMap.put("darkblue", "00008b");
		colorMap.put("navy", "000080");
		colorMap.put("royalblue", "4169e1");
		colorMap.put("cornflowerblue", "6495ed");
		colorMap.put("lightsteelblue", "b0c4de");
		colorMap.put("lightslategray", "778899");
		colorMap.put("slategray", "708090");
		colorMap.put("doderblue", "1e90ff");
		colorMap.put("aliceblue", "f0f8ff");
		colorMap.put("steelblue", "4682b4");
		colorMap.put("lightskyblue", "87cefa");
		colorMap.put("skyblue", "87ceeb");
		colorMap.put("deepskyblue", "00bfff");
		colorMap.put("lightblue", "add8e6");
		colorMap.put("powderblue", "b0e0e6");
		colorMap.put("cadetblue", "5f9ea0");
		colorMap.put("azure", "f0ffff");
		colorMap.put("lightcyan", "e1ffff");
		colorMap.put("paleturquoise", "afeeee");
		colorMap.put("cyan", "00ffff");
		colorMap.put("aqua", "00ffff");
		colorMap.put("darkturquoise", "00ced1");
		colorMap.put("darkslategray", "2f4f4f");
		colorMap.put("darkcyan", "008b8b");
		colorMap.put("teal", "008080");
		colorMap.put("mediumturquoise", "48d1cc");
		colorMap.put("lightseagreen", "20b2aa");
		colorMap.put("turquoise", "40e0d0");
		colorMap.put("auqamarin", "7fffaa");
		colorMap.put("mediumaquamarine", "00fa9a");
		colorMap.put("mediumspringgreen", "f5fffa");
		colorMap.put("mintcream", "00ff7f");
		colorMap.put("springgreen", "3cb371");
		colorMap.put("seagreen", "2e8b57");
		colorMap.put("honeydew", "f0fff0");
		colorMap.put("lightgreen", "90ee90");
		colorMap.put("palegreen", "98fb98");
		colorMap.put("darkseagreen", "8fbc8f");
		colorMap.put("limegreen", "32cd32");
		colorMap.put("lime", "00ff00");
		colorMap.put("forestgreen", "228b22");
		colorMap.put("green", "008000");
		colorMap.put("darkgreen", "006400");
		colorMap.put("chartreuse", "7fff00");
		colorMap.put("lawngreen", "7cfc00");
		colorMap.put("greenyellow", "adff2f");
		colorMap.put("olivedrab", "556b2f");
		colorMap.put("beige", "6b8e23");
		colorMap.put("lightgoldenrodyellow", "fafad2");
		colorMap.put("ivory", "fffff0");
		colorMap.put("lightyellow", "ffffe0");
		colorMap.put("yellow", "ffff00");
		colorMap.put("olive", "808000");
		colorMap.put("darkkhaki", "bdb76b");
		colorMap.put("lemonchiffon", "fffacd");
		colorMap.put("palegodenrod", "eee8aa");
		colorMap.put("khaki", "f0e68c");
		colorMap.put("gold", "ffd700");
		colorMap.put("cornislk", "fff8dc");
		colorMap.put("goldenrod", "daa520");
		colorMap.put("floralwhite", "fffaf0");
		colorMap.put("oldlace", "fdf5e6");
		colorMap.put("wheat", "f5deb3");
		colorMap.put("moccasin", "ffe4b5");
		colorMap.put("orange", "ffa500");
		colorMap.put("papayawhip", "ffefd5");
		colorMap.put("blanchedalmond", "ffebcd");
		colorMap.put("navajowhite", "ffdead");
		colorMap.put("antiquewhite", "faebd7");
		colorMap.put("tan", "d2b48c");
		colorMap.put("brulywood", "deb887");
		colorMap.put("bisque", "ffe4c4");
		colorMap.put("darkorange", "ff8c00");
		colorMap.put("linen", "faf0e6");
		colorMap.put("peru", "cd853f");
		colorMap.put("peachpuff", "ffdab9");
		colorMap.put("sandybrown", "f4a460");
		colorMap.put("chocolate", "d2691e");
		colorMap.put("saddlebrown", "8b4513");
		colorMap.put("seashell", "fff5ee");
		colorMap.put("sienna", "a0522d");
		colorMap.put("lightsalmon", "ffa07a");
		colorMap.put("coral", "ff7f50");
		colorMap.put("orangered", "ff4500");
		colorMap.put("darksalmon", "e9967a");
		colorMap.put("tomato", "ff6347");
		colorMap.put("mistyrose", "ffe4e1");
		colorMap.put("salmon", "fa8072");
		colorMap.put("snow", "fffafa");
		colorMap.put("lightcoral", "f08080");
		colorMap.put("rosybrown", "bc8f8f");
		colorMap.put("indianred", "cd5c5c");
		colorMap.put("red", "ff0000");
		colorMap.put("brown", "a52a2a");
		colorMap.put("firebrick", "b22222");
		colorMap.put("darkred", "8b0000");
		colorMap.put("maroon", "800000");
		colorMap.put("white", "ffffff");
		colorMap.put("whitesmoke", "f5f5f5");
		colorMap.put("gainsboro", "dcdcdc");
		colorMap.put("lightgrey", "d3d3d3");
		colorMap.put("silver", "c0c0c0");
		colorMap.put("darkgray", "a9a9a9");
		colorMap.put("gray", "808080");
		colorMap.put("dimgray", "696969");
		colorMap.put("black", "000000");
	}
/*
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getImageAndCleanHtml(String html, String imgAddr, Long contId) {
		ContentCacheManager ccm = (ContentCacheManager) PortalConstants.ctx.getBean("contentCacheManager");
		Content cont = ccm.get(contId);
		List<Map<String, Object>> result = null;
		List<Image> imageList = null;
		if (cont != null && cont.getField("CONTPARSER") != null && cont.getField("CONTPARSER") instanceof List) {
			result = (List<Map<String, Object>>) cont.getField("CONTPARSER");
		} else {
			if (cont != null && cont.getField("IMAGES") instanceof List) {
				imageList = (List<Image>) cont.getField("IMAGES");
			}
			result = getImageAndCleanHtml(html, imgAddr, imageList);
			cont.setField("CONTPARSER", result);
			ccm.update(contId, cont);
		}
		return result;
	}*/

	public static List<Map<String, Object>> getImageAndCleanHtml(String html, List<AssetImage> images) {
		return getImageAndCleanHtml(html, null, images);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map<String, Object>> getImageAndCleanHtml(String html, String imgAddr, List<AssetImage> images) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> imgListUrl;
		List<Map<String, String>> imageInfoList;
		imgAddr = null == imgAddr ? "" : imgAddr.trim();

		html = html.replaceAll(DIV_REGX, BR_REPLACE + "$1" + BR_REPLACE);
		try {
			Matcher m = IMG_P.matcher(html);
			String temp = html;
			while (m.find()) {
				String imgLable = m.group(0);
				String imgUrl = m.group(1);
				int idx = temp.indexOf(imgLable);
				if (idx > 0) {
					String content = cleanHtml(temp.substring(0, idx));
					if (content.trim().length() > 0) {
						if (map.size() > 0)
							list.add(map);
						map = new HashMap<String, Object>();
						map.put("content", content);
					}
				}
				imgListUrl = (List) map.get("imgListUrl");
				if (imgListUrl == null) {
					imgListUrl = new ArrayList<String>();
					map.put("imgListUrl", imgListUrl);
				}
				String imgAddrUrl = imgAddr + imgUrl;
				if (imgUrl.startsWith("http://"))
					imgAddrUrl = imgUrl;
				imgListUrl.add(imgAddrUrl);

				imageInfoList = (List) map.get("imageInfoList");
				if (imageInfoList == null) {
					imageInfoList = new ArrayList<Map<String, String>>();
					map.put("imageInfoList", imageInfoList);
				}
				Map<String, String> imageMap = new HashMap<String, String>();
				imageMap.put("url", imgAddrUrl);
				if (images != null) {
					for (AssetImage image : images) {
						if (imgAddrUrl.endsWith(JsonUtil.str2json(image.getMpath()))) {
							if (StringUtil.isNullStr(imgAddr))
								imageMap.put("url", image.getMpath());
							imageMap.put("name", image.getName());
//							imageMap.put("desc", image.getDescription());
//							imageMap.put("tags", image.getTags());
							imageMap.put("width", StringUtil.null2Str(image.getWidth()));
							imageMap.put("height", StringUtil.null2Str(image.getHeight()));
						}
					}
				}
				imageInfoList.add(imageMap);
				temp = temp.substring(idx + imgLable.length()).trim();
			}
			if (map.size() > 0)
				list.add(map);
			if (temp != null && temp.trim().length() > 0) {
				String content = cleanHtml(temp);
				if (content.trim().length() > 0) {
					map = new HashMap<String, Object>();
					map.put("content", cleanHtml(temp));
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String cleanHtml(String html) {
		if (html == null || html.trim().length() == 0)
			return "";

		// 替换空格
		html = html.replaceAll(SPACE_REGX, SPACE_REPLACE);
//		html = html.replaceAll(SPACE_HTML_REGX, SPACE_REPLACE);
		// 替换换行
		html = html.replaceAll(BR_REGX, BR_REPLACE);

		// 替换加粗
		html = html.replaceAll(STRONG_BEGIN_REGX, STRONG_BEGIN_REPLACE);
		html = html.replaceAll(STRONG_END_REGX, STRONG_END_REPLACE);

		// 替换下滑线
		html = html.replaceAll(ULINE_BEGIN_REGX, ULINE_BEGIN_REPLACE);
		html = html.replaceAll(ULINE_END_REGX, ULINE_END_REPLACE);

		// 转化颜色
		html = translateColoreValue(html);

		// 清空html注释
		html = html.replaceAll(HTML_NOTES_REGX, "");

		// 清空html标签
		html = html.replaceAll(HTML_LABEL_REGX, "");

		// 替换转义符
		html = html.replaceAll(LEFT_ESCAPE, LEFT_REPLACE);
		html = html.replaceAll(RIGHT_ESCAPE, RIGHT_REPLACE);
		html = html.replaceAll(HTML_ESCAPE, HTML_ESCAPE_REPLACE);
		html = html.replaceAll("[\\u3000]", "  ");
		html = html.trim();

		// 清除段首段尾的空格符
		Matcher m = WDML_P.matcher(html);
		while (m.find()) {
			String wdmlLable = m.group(0);
			String trim_wdmlLable = wdmlLable.replaceAll("\\s", "");
			html = html.replace(wdmlLable, trim_wdmlLable);
		}
		html = html.replaceAll("\\s*\\\\n\\s*", "\\\\n");

		// 清除多余换行符
		html = html.replaceAll("(\\\\n){3,}", "\\\\n\\\\n");

		// html = html.replaceAll("(\\\\n){1,}<B>(\\\\n){1,}", "\\\\n<B>");
		// html = html.replaceAll("(\\\\n){1,}</B>(\\\\n){1,}", "</B>\\\\n");

		// 去掉首尾的换行符
		html = html.replaceAll("^(\\\\n)+", "");
		html = html.replaceAll("(\\\\n)+$", "");
		return html;
	}

	public static String translateColoreValue(String html) {
		Matcher m = SPAN_P.matcher(html);
		while (m.find()) {
			String src = m.group(0);
			Map<String, String> styles = parseStyle2Map(m.group(1));
			String content = m.group(2);
			String color = getColoreValue(styles.get("color"));
			String dest = content;
			if (!StringUtil.isNullStr(color)){
				dest = "<C:" + color + ">" + content + "<N>";
			}
			html = html.replace(src, dest);
		}
		return html;
	}

	public static Map<String, String> parseStyle2Map(String styleStr) {
		Map<String, String> styles = new HashMap<String, String>();
		if (styleStr != null) {
			String[] arr1 = styleStr.split("[;]");
			if (arr1 != null && arr1.length > 0) {
				for (String prop : arr1) {
					String[] arr2 = prop.split(":");
					if (arr2 != null && arr2.length > 1) {
						styles.put(arr2[0].toLowerCase().trim(), arr2[1].trim());
					}
				}
			}
		}
		return styles;
	}

	public static String getColoreValue(String color) {
		if (color == null)
			return "";
		Matcher m = RGB_FUNC_P.matcher(color);
		if (m.find()) {
			int r = StringUtil.nullToInteger(m.group(1));
			int g = StringUtil.nullToInteger(m.group(2));
			int b = StringUtil.nullToInteger(m.group(3));
			color = toHexString(r) + toHexString(g) + toHexString(b);
		} else if (color.startsWith("#")) {
			color = color.substring(1);
		} else if (colorMap.containsKey(color.toLowerCase())) {
			color = colorMap.get(color.toLowerCase());
		}
		return color;
	}

	private static String toHexString(int i) {
		String s = Integer.toHexString(i);
		if (s.length() == 1)
			s = "0" + s;
		else if (s.length() > 2)
			s = s.substring(s.length() - 2);
		return s;
	}

	public static void main(String[] args) {
//		String html = "　　美国得克萨斯州卫生部门10月15日证实，又有一名与埃博拉患者有密切接触的医护人员，被检测出感染该病毒。13日，这名医护人员乘坐过一架载有132人的客机。<br />　　她的祖母向路透社确认了这一不幸消息。该女孩叫安珀·文森（Amber Vinson），今年29岁。据NBC10月16日最新消息称，这名女医护人员乘飞机是去克利夫兰看望家人，并且商议自己结婚事宜。<br />　　据中新网消息，美国疾控中心(CDC)15日宣布，该患者14日出现症状的前一天曾乘坐从克利夫兰到达拉斯的飞机，目前呼吁航班所有旅客与当局联系。<br />　　达拉斯县法官詹金斯(Clay Jenkins)在15日的新闻发布会上表示，这名尚未公开姓名的女性医护工作者，像女护士范妮娜(Nina Pham)一样曾在达拉斯得州卫生长老会医院照顾美国首例埃博拉患者邓肯(Thomas Eric Duncan)。在进行埃博拉症状的自我监测中，她于14日报告发烧，90分钟内被隔离。先后经得州奥斯汀健康实验室和位于亚特兰大的美国疾控中心检测，其埃博拉病毒呈阳性。她也成为美国境内感染埃博拉的第二人。<br />　　美国疾控中心15日发布消息称，这名染病医护人员曾经于10日乘坐边境航空公司1142航班，从达拉斯/沃斯堡国际机场飞往克利夫兰，并于发病前的13日乘坐该航空公司1143航班返回达拉斯。按照正常程序，该架航班13日当晚进行了彻底清洁，并于第二天继续飞行服务。<br />　　边境航空公司15日立即回应疾控中心，“根据空乘人员描述，该名旅客乘坐1143航班时没有表现出任何患病症状”。航空公司将该架飞机从服务列表移除，并将与疾控中心密切合作，识别和联系乘坐过1143航班的132名乘客。<br />　　疾控中心呼吁，曾经乘坐过上述两架航班的所有旅客及时与CDC取得联系，“任何有潜在传染风险者都将被积极监测”。<br />　　来自边境航空的官方声明称，该乘客在1143次航班上并未显示出患病症状。在CDC通知后边境航空公司快速反应取消了该航班后续的飞行计划。边境航空现与CDC捷易通进行调查，确认乘坐1143次航班的乘客身份。<br />    达拉斯市市长罗林斯(Mike Rawlings)15日对媒体表示，目前情况“可能不会好转，反而会变得更糟”。达拉斯市政当局正在通知第二名染病医护人员的邻居；她一人独居，没有养宠物。目前正在对其住处进行消毒。<br />　　此前，来自利比里亚的邓肯是第一个在美国被诊断出的埃博拉患者，他于10月8日在卫生长老会医院不治身亡。照料邓肯的过程中，越南裔重症护理护士范妮娜受到感染。　<br />　　美国卫生官员称，可能有76名医护人员在照料邓肯的过程中接触了病毒，因而对他们进行隔离观察。<br />　　就在第二名医护人员确诊染病前几小时，达拉斯得州卫生长老会医院还遭到全美护士联盟的批评，后者指责该院没有处理这种疾病的适当协议，并称在邓肯被确诊后的几天内护士们曾抱怨医院混乱和缺乏培训，使得护理人员面临风险。";
//		System.out.println(System.currentTimeMillis());
//		System.out.println(cleanHtml(html));
//		System.out.println(System.currentTimeMillis());
//		String html = "开始<span style=\"font-size: 11.8181819915771px; color: rgb(128, 128, 128);\">2007年7月，陈冲在上海出席“携手儿童青少年，携手抗击爱滋病 ”的活动。 高剑平 澎湃资料</span>结束";
//		System.out.println(translateColoreValue(html));
//		System.out.println(cleanHtml(html));
		
		String html = FileIO.file2String("C:/Users/chyx/Desktop/SSHDL/news.html");

		long start = System.currentTimeMillis();
		List<Map<String, Object>> list = getImageAndCleanHtml(html, null ,new ArrayList());
		System.out.println(System.currentTimeMillis() - start);
//		System.out.println(list);
		
		System.out.println(com.wondertek.mam.util.JsonUtil.list2json(list));
	}
}
