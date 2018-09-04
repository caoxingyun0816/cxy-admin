package com.wondertek.mam.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mam.cache.SystemConfigCache;
import com.wondertek.mam.commons.MamConstants;
import com.wondertek.mam.util.file.FilePathHelper;
import com.wondertek.mobilevideo.core.util.HttpClientUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;

public class SourceUtil {
	private final static Log log = LogFactory.getLog(SourceUtil.class);
	private final static String[] SC_VIDEO_SIGN = {"_sc99_","_sc98_","_sc3d_","_scvr_"};
	private final static String[] SDP_S3U8 = {".sdp",".m3u8"};
	private final static String SC_VIDEO_PATTERN = ".*\\_sc|\\_.*";
	private final static String EC_VIDEO_PATTERN = ".*\\_|\\..*";
	
	
	public static String idToPath(Long id) {
		String idStr = String.valueOf(id);
		if(idStr.length() < 10) {
			return idStr;
		}
		return idStr.substring(0, 4)+"/"+idStr.substring(4, 7)+"/"+idStr.substring(7);
	}
	
	public static String idToFullPath(Long id) {
		String idStr = String.valueOf(id);
		if(idStr.length() < 10) {
			return idStr;
		}
		return idStr.substring(0, 4)+"/"+idStr.substring(4, 7)+"/"+idStr.substring(7)+"/"+idStr;
	}
	
	public static String idToFullPath(String id) {
		String idStr = String.valueOf(id);
		if(idStr.length() < 10) {
			return idStr;
		}
		return idStr.substring(0, 4)+"/"+idStr.substring(4, 7)+"/"+idStr.substring(7)+"/"+idStr;
	}
	/**
	 * 获取媒资采编区路径
	 * *因store存储在龙存，store区为可配cfg_path，仅限迁移使用
	 * @param assetId
	 * @return
	 */
	public static String getStoreDir(Long assetId) {
		
		String tarPath = MamConstants.STORE_PATH + "/" + MamConstants.DIR_ASSET + "/" + MamConstants.DIR_ZHENGSHI + "/" + SourceUtil.idToFullPath(assetId);
		
		return tarPath;
	}
	/**
	 * *因store存储在龙存，store区为可配cfg_path，仅限迁移使用
	 * @param assetId
	 * @return
	 */
	public static String getStoreDir(String assetId) {
		
//		String tarPath = MamConstants.STORE_PATH + "/" + MamConstants.DIR_ASSET + "/" + MamConstants.DIR_ZHENGSHI + "/" + SourceUtil.idToFullPath(assetId);
		String tarPath = MamConstants.STORE_PATH + "/" + MamConstants.DIR_ASSET + "/" + MamConstants.DIR_ZHENGSHI + "/" + SourceUtil.idToFullPath(assetId);

		return tarPath;
	}
	
	/**
	 * 获取媒资发布区路径
	 * @param assetId
	 * @return
	 */
	public static String getAssetDir(Long assetId, String cfgPath) {
//		String tarPath = MamConstants.DEPOSITORY_PATH + "/" + MamConstants.DIR_ASSET + "/" + MamConstants.DIR_ZHENGSHI + "/" + SourceUtil.idToFullPath(assetId);
		
		String tarPath = FilePathHelper.joinPath(MamConstants.REAL_PATH, StringUtil.isNullStr(cfgPath) ? MamConstants.DEPOSITORY : cfgPath, 
				MamConstants.DIR_ASSET, MamConstants.DIR_ZHENGSHI, SourceUtil.idToFullPath(assetId));
		return tarPath;
	}
	
	public static String getAssetDir(String assetId, String cfgPath) {
//		String tarPath = MamConstants.DEPOSITORY_PATH + "/" + MamConstants.DIR_ASSET + "/" + MamConstants.DIR_ZHENGSHI + "/" + SourceUtil.idToFullPath(assetId);
		String tarPath = FilePathHelper.joinPath(MamConstants.REAL_PATH, StringUtil.isNullStr(cfgPath) ? MamConstants.DEPOSITORY : cfgPath, 
				MamConstants.DIR_ASSET, MamConstants.DIR_ZHENGSHI, SourceUtil.idToFullPath(assetId));
		return tarPath;
	}
	
	/**
	 * 获取内容发布区路径
	 * @param contId
	 * @return
	 */
	public static String getContDir(Long contId) {
		
		String tarPath = MamConstants.DEPOSITORY_PATH + "/" + MamConstants.DIR_CONT + "/" + MamConstants.DIR_ZHENGSHI + "/" + idToFullPath(contId);
		
		return tarPath;
	}
	
	public static String getContDir(String contId) {
		
		String tarPath = MamConstants.DEPOSITORY_PATH + "/" + MamConstants.DIR_CONT + "/" + MamConstants.DIR_ZHENGSHI + "/" + idToFullPath(contId);
		
		return tarPath;
	}
	
	public static String getCPSourceRoot(String cpid, String ftpChannel) {
		String slnk = MamConstants.FTP_LINK;
		if(StringUtil.isNullStr(ftpChannel) || "0".equals(ftpChannel)) {
			//do nothing
		}else {
			slnk = MamConstants.FTP_LINK+"_"+ftpChannel;
		}
		return MamConstants.SOURCE_PATH + "/" + slnk + "/" + StringUtil.null2Str(cpid);
	}
	
	public static boolean isSdpOrM3u8 (String fileName) {
		boolean flag = false;
		for(String sign : SDP_S3U8) {
			if(fileName.contains(sign)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	
	/**
	 * 根据媒体文件命名规范判断是否为源文件
	 * @param fileName
	 * @return
	 */
	public static boolean isSource(String fileName) {
		boolean flag = false;
		
		for(String sign : SC_VIDEO_SIGN) {
			if(fileName.contains(sign)) {
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * 根据媒体文件命名规范判断是否为转码后文件
	 * @param fileName
	 * @return
	 */
	public static boolean isEncoding(String fileName) {
		boolean flag = false;
		boolean isSc = false;
		
		for(String sign : SC_VIDEO_SIGN) {
			if(fileName.contains(sign)) {
				isSc = true;
				break;
			}
		}
		if(!isSc) {
			String uc = fileName.replaceAll(EC_VIDEO_PATTERN, "");
			
			if(StringUtil.isNumber(uc)) {//如果不是数字，则失败
				flag = true;
			}
		}
		
		return flag;
	}
	
	public static String getUsageCode(String fileName) {
		String uc = null;
		if(isSource(fileName)) {
			uc = fileName.replaceAll(SC_VIDEO_PATTERN, "");
		} else {
			uc = fileName.replaceAll(EC_VIDEO_PATTERN, "");
		}
		
		if(!StringUtil.isNumber(uc) && !"3d".equals(uc) && !"vr".equals(uc)) {//如果不是数字，则失败
			uc = null;
		}
		return uc;
	}
	/**
	 * 获取dams分发文件路径
	 * @param mpath assetMedia.mpath
	 * @param assetId asset.assetId
	 * @param type asset.type
	 * @return
	 */
	public static String damsDistUrl(String mpath, Long assetId, String type, String cfgPath) {
		String cmsUrl = mpath;
		String prepath = SystemConfigCache.getValue(MamConstants.DIST_MEDIA_PATHPRE);
		if("7".equals(type)) {
			if(mpath.startsWith("opt/swap")||mpath.startsWith("/opt/swap")) {
				//do nothing
			}else {
				if(mpath.startsWith("zhengshi")||mpath.startsWith("/zhengshi")) {//兼容2开头的媒资
					//do nothing
				}else {
					cmsUrl= prepath.replace("depository", StringUtil.isNullStr(cfgPath) ? "depository" : cfgPath)+mpath;
				}
			}
		}
		return cmsUrl;
	}
	
	/**
	 * 获取源片、转码后文件相对路径
	 * @param mpath assetMedia.mpath
	 * @param assetId asset.assetId
	 * @param type asset.type
	 * @param cfgPath assetMedia.cfgPath
	 * @param isSource assetMedia.isSource
	 * @return
	 */
	public static String damsDistUrl(String mpath, Long assetId, String type, String cfgPath, boolean isSource) {
		String cmsUrl = mpath;
		String prepath = SystemConfigCache.getValue(MamConstants.DIST_MEDIA_PATHPRE);
		if("7".equals(type)) {
			if(mpath.startsWith("opt/swap")||mpath.startsWith("/opt/swap")) {
				//do nothing
			}else {
				if(mpath.startsWith("zhengshi")||mpath.startsWith("/zhengshi")) {//兼容2开头的媒资
					//do nothing
				}else {
					if(isSource) {
						String pathRoot = "";
						File f = new File(FilePathHelper.joinPath(MamConstants.STORETMP_PATH, MamConstants.DIR_ASSET, MamConstants.DIR_ZHENGSHI, mpath));
		                if (f.exists()) {
		                    pathRoot = MamConstants.STORE_TMP;
		                } else {
		                    pathRoot = StringUtil.isNullStr(cfgPath) ? MamConstants.STORE : cfgPath;
		                }
		                cmsUrl = "/"+FilePathHelper.joinPath(pathRoot, MamConstants.DIR_ASSET, MamConstants.DIR_ZHENGSHI, mpath);
					}else {
						cmsUrl= prepath.replace("depository", StringUtil.isNullStr(cfgPath) ? "depository" : cfgPath)+mpath;
					}
				}
			}
		}
		return cmsUrl;
	}
	
	/**
	 * 获取播放地址
	 * @param mpath assetMedia.mpath
	 * @param type asset.type
	 * @param isUrlOnly assetMedia.isUrlOnly
	 * @return
	 */
	public static String damsPlayUrl(String mpath, String type, Boolean isUrlOnly, String cfgPath) {
		String prePlayUrl = SystemConfigCache.getValue(MamConstants.DIST_PLAY_URL);
		if(StringUtil.isNullStr(prePlayUrl)) {
			prePlayUrl = "rtsp://172.16.10.205/";
		}
		String playUrl = "";
		if(StringUtil.nullToBoolean(isUrlOnly)){
			playUrl = mpath;
		}else{
			if(SourceUtil.isMigrMedia(mpath) || !"7".equals(type)) {
				playUrl = FilePathHelper.joinPath(prePlayUrl, mpath);
			} else {
				playUrl = FilePathHelper.joinPath(prePlayUrl, StringUtil.isNullStr(cfgPath) ? "depository" : cfgPath,"/asset/zhengshi", mpath);
			}
		}
		return playUrl;
	}
	
	public static boolean isMigrMedia(String mpath) {
		boolean flag = false;
		if(!StringUtil.isNull(mpath) && (mpath.startsWith("opt/swap")||mpath.startsWith("/opt/swap")||mpath.startsWith("zhengshi")||mpath.startsWith("/zhengshi"))) {
			flag = true;
		}
		return flag;
	}
	/**
	 * 获取迁移(20,22,21,3等开头的媒资)的媒体文件存储路径
	 * @param mpath
	 * @return
	 */
	public static String getMigrPath(String mpath) {
		if(!StringUtil.isNull(mpath)) {
			if(mpath.startsWith("opt/swap")||mpath.startsWith("/opt/swap")) {
				return FilePathHelper.joinPath(mpath);
			}else if(mpath.startsWith("zhengshi")||mpath.startsWith("/zhengshi")) {
				return FilePathHelper.joinPath(MamConstants.DEPOSITORY_PATH, MamConstants.DIR_CONT, mpath);
			}
		}
		return mpath;
	}
	
	public static String newsVideoSrc(String news, String[] src) {
		if(news != null) {
			news = news.replace(" src ", " src=\"\" ");//仅适用于一个视频 <video src ></video>
			
			Pattern p = Pattern.compile("<video.*src\\s*=\\s*(.*?)[^>]*?video>", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(news);
			int i=0;
			while (m.find()) {
				String dom = m.group();
				
				Matcher mm = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(dom);
				
				while (mm.find()) {
					String osrc = mm.group();
					if(src.length > i) {
						news = news.replace(osrc, "src=\""+src[i]+"\"");
					}
				}
				
				i++;
			}
		}
		
		return news;
	}
	/**
	 * 0-长视频，1-短视频，2-合作方视频
	 * @return
	 */
	public static String getMamSystem() {
		String sys = "0";
		
		if("1".equals(SystemConfigCache.getValue("IS_CP_MAM"))) {//CP媒资
			sys = "2";
		}else if("1".equals(MamConstants.IS_SIMPLE_CLASS)) {
			sys = "1";
		}
		
		return sys;
	}
	
	public static String getFirstPublishSource() {
		String firstPublish = SystemConfigCache.getValue("FIRST_PUBLISH_SOURCE");
		if(StringUtil.isNullStr(firstPublish) && "1".equals(MamConstants.IS_SIMPLE_CLASS)) {
			firstPublish = "601537,601538,601465";//AI、云快编、天脉
		}
		return firstPublish;
	}
	
	/**
	 * 获取文件或文件夹大小
	 * @param file
	 * @return
	 */
	public static long getTotalSizeOfFilesInDir(File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
	/**
	 * 获取txt编码
	 * @param txt
	 * @return
	 * @throws IOException
	 */
	public static String getCharset(File txt) throws IOException{
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(txt));  
        int p = (bin.read() << 8) + bin.read();  
        
        String code = null;  
        
        switch (p) {  
            case 0xefbb:  
                code = "UTF-8";  
                break;  
            case 0xfffe:  
                code = "Unicode";  
                break;  
            case 0xfeff:  
                code = "UTF-16BE";  
                break;  
            default:  
                code = "GBK";  
        }
        return code;
	}
	
	/**
	 * 表示内容的来源
	 * 2：批量内容注入
	 * 3:今日头条图文
	 * 4:今日头条视频
	 * 5:凤凰网
	 * 6:时光网
	 * 7:电影网
	 * 8:YouTube
	 */
	public static String adaptImported(String site, String category) {
		String imported = "9";
		if(MamConstants.TOUTIAO.equals(site)) {//头条
			if("3".equals(category)) {
				imported = "3";
			} else {
				imported = "4";
			}
		} else if(MamConstants.IFENG.equals(site)) {//凤凰
			imported = "5";
		} else if(MamConstants.MTIME.equals(site)) {//时光
			imported = "6";
		} else if(MamConstants.DIANYING.equals(site)) {//1905电影网
			imported = "7";
		} else if(MamConstants.YOUTUBE.equals(site)) {//YouTube
			imported = "8";
		}
		
		return imported;
	}
	
	public static List<Long> groupIdsToList(String groupIds) {
		List<Long> list = new ArrayList<Long>();
		if (groupIds == null || "".equals(groupIds) == true)
			return list;

		groupIds = groupIds.replace(",,", ",");
		if (groupIds.startsWith(","))
			groupIds = groupIds.substring(1, groupIds.length());
		if (groupIds.endsWith(","))
			groupIds = groupIds.substring(0, groupIds.length() - 1);

		String[] gIds = groupIds.split(",");
		if (gIds != null && gIds.length > 0) {
			for (int i = 0; i < gIds.length; i++) {
				if (gIds[i] != null && NumberUtils.isNumber(gIds[i])) {
					list.add(Long.valueOf(gIds[i]));
				}
			}
		}
		return list;
	}

	public static String listToGroupIds(List<Long> list, Map<Long, String> groupMap) {
		if (list == null || list.size() == 0)
			return null;
		if (groupMap == null || groupMap.size() == 0)
			return null;

		boolean isStrAvalid = false;
		StringBuffer sb = new StringBuffer();
		for (Iterator<Long> it = list.iterator(); it.hasNext();) {
			Long groupId = it.next();
			if (groupId != null && groupMap.containsKey(groupId)) {
				sb.append("," + groupId);
				isStrAvalid = true;
			}
		}

		if (isStrAvalid)
			sb.append(",");
		return sb.toString();
	}

	public static void initUserAndGroupInfoMap(String casUserInfo) {
		try {
			log.error("UserAndGroupInfo[URL=" + casUserInfo + "]");
			String userAndGroupInfo = HttpClientUtil.requestGet(casUserInfo);

			// 获取ALL用户信息
			int start = userAndGroupInfo.indexOf("<cas:users>") + "<cas:users>".length();
			int end = userAndGroupInfo.indexOf("</cas:users>");
			if (start > 0 && start < end) {
				String allUserInfo = userAndGroupInfo.substring(start, end);
				String[] users = allUserInfo.split(",");
				if (users != null && users.length > 0) {
					MamConstants.ALL_USER.clear();
					for (String user : users) {
						String[] idname = user.split("=");
						if (idname != null && idname.length == 2) {
							MamConstants.ALL_USER.put(StringUtil.stringToLong(idname[0]), idname[1]);
						}
					}
				}
			}

			// 获取ALL群组信息
			int start2 = userAndGroupInfo.indexOf("<cas:groups>") + "<cas:groups>".length();
			int end2 = userAndGroupInfo.indexOf("</cas:groups>");
			if (start2 > 0 && start2 < end2) {
				String allGroupInfo = userAndGroupInfo.substring(start2, end2);
				String[] groups = allGroupInfo.split(",");
				if (groups != null && groups.length > 0) {
					MamConstants.ALL_GROUP.clear();
					for (String user : groups) {
						String[] idname = user.split("=");
						if (idname != null && idname.length == 2) {
							MamConstants.ALL_GROUP.put(StringUtil.stringToLong(idname[0]), idname[1]);
						}
					}
				}
			}
			log.info("initUserAndGroupInfoMap[userSize=" + MamConstants.ALL_USER.size() + ", groupSize=" + MamConstants.ALL_GROUP.size() + "]");
		} catch (Exception e) {
			log.error("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.error("+                                                                 +");
			log.error("", e);
			log.error("+                                                                 +");
			log.error("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
	}
	/**
	 * 去除字符串的换行符
	 * @param str
	 * @return
	 */
	 public static String replaceBlank(String str) {
		 if (!ST.isNull(str)) {
			 Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			 Matcher m = p.matcher(str);
			 str = m.replaceAll("");
		 }
		 return str;
	 }
	 /**
	  * 根据m3u8文件查找m3u8文件夹
	  * @param m3u8File
	  * @return
	  */
	  public static File findM3u8Dir(File m3u8File) {
	 	File m3u8Dir=null;	
		if (m3u8File!=null&&m3u8File.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(
										m3u8File), "utf-8"));
				String a = null;
				File[] files = m3u8File.getParentFile()
						.listFiles();
				boolean breakWhile = false;
				while ((a = br.readLine()) != null) {
					for (File f : files) {
						if (f.isDirectory() && a.indexOf(f.getName()+"/") != -1) {
							m3u8Dir = f;
							breakWhile = true;
							break;
						}
					}
					if (breakWhile == true) {
						break;
					}
				}

			} catch (Exception e) {
			    e.printStackTrace();  
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (Exception e) {
                     e.printStackTrace();
				}
			}
		}
		
	 	 return m3u8Dir;
	  }
	 
}
