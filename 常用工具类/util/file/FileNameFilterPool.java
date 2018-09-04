package com.wondertek.mam.util.file;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.lang.StringUtils;

import com.wondertek.mam.cache.SystemConfigCache;
import com.wondertek.mam.commons.MamConstants;

/**
 * 扫描文件的过滤器
 * 
 * @author Administrator
 * 
 */
public class FileNameFilterPool
{
	public static final int FILETYPE_VOD = 1; 
	public static final int FILETYPE_LIVE = 2; 
	public static final int FILETYPE_DOWNLOAD = 3;
	public static final int FILETYPE_VOD_DOWNLOAD = 4 ;
	public static final int FILETYPE_VOD_LIVE = 5 ;
	public static final int FILETYPE_CONTSET = 6 ;
	public static final int FILETYPE_AUD = 7; 
	public static final int FILETYPE_SUBTITLE = 98 ;
	public static final int FILETYPE_IMAGE = 99 ;
	
	public static final boolean IS_SHOW_DIRECTORY = true ;
	public static final String MEDIA_POSTFIX = "flv,mp4,3gp,mpg,mpeg,wmv,ts,mkv,m2t";
	public static final String AUDIO_POSTFIX = "mpg,mp3,aac,m4a,amr,wma";
	public static final String SUBTITLE_POSTFIX = "srt,xml,ass";
	public static final String PIC_POSTFIX = "gif,jpg,bmp,png" ;
	public static final String LIVE_POSTFIX = "sdp,xml";
	public static final String VODLIVE_POSTFIX = "smil";
	public static final String CONTSET_POSTFIX = "zip,rar";
	
	public static FileFilter getFileFilter(String regex , int...type)
	{
		return new MediaFileFilter(regex , type);
	}
	/**
	 * 内容媒体文件后缀过滤器
	 * 
	 * @param contType
	 *            ：1-点播类（含下载），2-直播，5-模拟直播，6-内容集，
	 * @return
	 */
	public static FileFilter getContentFileFilter(int contType)
	{
		return new MediaFileFilter(contType);
	}
	
	/**
	 * 获得所有点播类型的文件过滤器，包括：视频文件、音频文件、字幕文件、图片。
	 * @return
	 */
	public static FileFilter getVodAllFilter()
	{
		return new MediaFileFilter(FILETYPE_VOD, FILETYPE_AUD, FILETYPE_SUBTITLE, FILETYPE_IMAGE);
	}
	/**
	 * 视频和图片
	 * @return
	 */
	public static FileFilter getVodImgFilter()
	{
		return new MediaFileFilter(FILETYPE_VOD, FILETYPE_IMAGE);
	}
	/**
	 * 音频
	 * @return
	 */
	public static FileFilter getAudFilter()
	{
		return new MediaFileFilter(FILETYPE_AUD);
	}
	/**
	 * 内容媒体文件后缀过滤器(含图片了)
	 * 
	 * @param contType
	 *            1-点播类（含下载），2-直播，5-模拟直播，6-内容集，
	 * @return
	 */
	public static FileFilter getCFAndImgFilter(int contType)
	{
		return new MediaFileFilter(contType, FILETYPE_IMAGE);
	}
	
	/**
	 * 获得媒体文件过滤器
	 * 
	 * @return
	 */
	public static FileFilter getMediaAndAudioFilter()
	{
		return new MediaFileFilter(FILETYPE_VOD, FILETYPE_AUD);
	}
	
	/**
	 * 获得媒体文件过滤器
	 * 
	 * @return
	 */
	public static FileFilter getMediaFilter()
	{
		return new MediaFileFilter(FILETYPE_VOD);
	}
	/**
	 * 获得字幕文件过滤器
	 * @return
	 */
	public static FileFilter getSubtitleFilter()
	{
		return new MediaFileFilter(FILETYPE_SUBTITLE);
	}
	/**
	 * 获得图片文件过滤器
	 * 
	 * @return
	 */
	public static FileFilter getPicFilter()
	{
		return new MediaFileFilter(FILETYPE_IMAGE);
	}

	/**
	 * 获得直播文件过滤器
	 * 
	 * @return
	 */
	public static FileFilter getLiveFilter()
	{
		return new MediaFileFilter(FILETYPE_LIVE);
	}
	/**
	 * 根据文件名获得文件类型，1-视频文件，98-字幕文件，99-图片
	 * @param fileName
	 * @return
	 */
	public static int getFileType(String fileName)
	{
		int[] types = {FILETYPE_VOD, FILETYPE_AUD, FILETYPE_LIVE, FILETYPE_SUBTITLE, FILETYPE_IMAGE};
		return filter(fileName,types);
	}
	/**
	 * 判断是否是媒体文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isMediaFile(String fileName)
	{
		return filter(fileName, FILETYPE_VOD) > 0 ? true : false;
	}
	
	/**
	 * 判断是否是音频文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isAudioFile(String fileName)
	{
		return filter(fileName, FILETYPE_AUD) > 0 ? true : false;
	}
	
	/**
	 * 判断是否是字幕文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isSubtitleFile(String fileName)
	{
		return filter(fileName, FILETYPE_SUBTITLE) > 0 ? true : false;
	}

	/**
	 * 判断是否是图片
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isPicFile(String fileName)
	{
		return filter(fileName, FILETYPE_IMAGE) > 0 ? true : false;
	}

	/**
	 * 判断是否是直播文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isLiveFile(String fileName)
	{
		return filter(fileName, FILETYPE_LIVE) > 0 ? true : false;
	}

	private static int filter(String fileName, int... fileTypes)
	{
		String[] pool;
		String postfix = StringUtils.substringAfterLast(fileName, ".");
		if (StringUtils.isBlank(postfix))
			return -1;
		for (int fileType : fileTypes)
		{
			pool = getFilters(fileType);

			// 找不到规则池，则默认匹配所有的。
			if (null == pool)
				return fileType;

			for (String pf : pool)
			{
				if (postfix.equalsIgnoreCase(pf))
					return fileType;
			}
		}
		return -1;
	}
	private static boolean filter(String regex , String fileName , int... fileTypes)
	{
		boolean result = true ;
		if (fileTypes != null && fileTypes.length > 0)
			result = filter(fileName, fileTypes) < 0 ? false : true ;
		if (result)
			result = regex != null && regex.length() > 0 && fileName.indexOf(regex) < 0 ? false : true ;
		return result ;
	}
	private static class MediaFileFilter implements FileFilter
	{
		private int[] type;
		private String regex ;
		
		MediaFileFilter(String regex, int... type)
		{
			this.regex = regex;
			this.type = type;
		}
		
		MediaFileFilter(int... type)
		{
			this.type = type;
		}

		@Override
		public boolean accept(File file)
		{
			if (file.isDirectory())
				return IS_SHOW_DIRECTORY;
			return filter(regex, file.getName(), type) ;
		}
	}

	private static String[] getFilters(int fileType)
	{
		String filters = null;
		switch (fileType)
		{
		case 1:
		case 3:
		case 4:
			filters = SystemConfigCache.getValueDefault(MamConstants.VIDEO_POSTFIX, MEDIA_POSTFIX);
			break;
		case 2:
			filters = SystemConfigCache.getValueDefault(MamConstants.LIVE_POSTFIX, LIVE_POSTFIX);
			break;
		case 5:
			filters = VODLIVE_POSTFIX;
			break;
		case 6:
			filters = CONTSET_POSTFIX;
			break;
		case 7:
			filters = SystemConfigCache.getValueDefault(MamConstants.AUDIO_POSTFIX, AUDIO_POSTFIX);
			break;
		case 98:
			filters = SystemConfigCache.getValueDefault(MamConstants.TITLE_FILE_POSTFIX, SUBTITLE_POSTFIX);
			break;
		case 99:
			filters = SystemConfigCache.getValueDefault(MamConstants.PIC_FILE_POSTFIX, PIC_POSTFIX);;
		}
		return null == filters ? null : filters.split("[,]");
	}
}
