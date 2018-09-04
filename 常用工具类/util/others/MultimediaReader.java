package com.wondertek.mam.util.others;

import it.sauronsoftware.jave.AudioInfo;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.FFMPEGLocator;
import it.sauronsoftware.jave.InputFormatException;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoInfo;
import it.sauronsoftware.jave.VideoSize;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.wondertek.mam.cache.SystemConfigCache;
import com.wondertek.mam.commons.MamConstants;
import com.wondertek.mam.commons.MamException;
import com.wondertek.mam.model.AssetImageApply;
import com.wondertek.mam.vo.ContentFile;
import com.wondertek.mobilevideo.core.util.StringUtil;

public class MultimediaReader {
	public static final int BYTEBIT = 8;
	private Map<String, String> formatMap = new HashMap<String, String>();
	private String ffmpegPath;
	private int kbit = 1000;
	
	private Encoder encoder;
	
	public MultimediaReader() {
		formatMap.put("matroska", "mkv");
	}
	
	private Encoder getEncoder() {
		if (encoder == null) {
			ffmpegPath = SystemConfigCache.getValue(MamConstants.FFMPEG_PATH);//最新ffmpeg路径
			if (!StringUtil.isNullStr(ffmpegPath))
				encoder = new Encoder(new FFMPEGLocator() {
					@Override
					protected String getFFMPEGExecutablePath() {
						return ffmpegPath;
					}
					
				});
			else
				encoder = new Encoder();
		}
		return encoder ;
	}

	private String getSimpleFormat(String format) {
		String tmp = formatMap.get(format.toLowerCase());
		return null == tmp ? format : tmp;
	}

	/**
	 * 设置图片属性
	 * 
	 * @param pf
	 *            picPath不能为空
	 * @throws MamException
	 */
	public  void setPicFileProp(AssetImageApply pf, File f) throws MamException
	{
		try
		{
			MultimediaInfo mi = this.getEncoder().getInfo(f);

			VideoInfo vi = mi.getVideo();
			if (null != vi)
			{
				VideoSize vs = vi.getSize();
				if (null != vs)
				{
//					pf.setResolution(String.format("%dx%d", vs.getWidth(), vs
//							.getHeight()));
					pf.setWidth(vs.getWidth());
					pf.setHeight(vs.getHeight());
				}
			}
		} catch (InputFormatException e)
		{
			throw new MamException(
					"it.sauronsoftware.jave.InputFormatException", e);
		} catch (EncoderException e)
		{
			throw new MamException("it.sauronsoftware.jave.EncoderException", e);
		}
	}

	/**
	 * 设置媒体文件属性
	 * 
	 * @param mf
	 *            其中filePath必须不能为空
	 * @throws MamException
	 */
	public void setMediaFileProp(ContentFile mf, File f) throws MamException {
		String format = org.apache.commons.lang.StringUtils.substringAfterLast(f.getName(), ".");
		mf.setFileSize(f.length());
		try
		{
			MultimediaInfo mi = this.getEncoder().getInfo(f);
			mf.setDuration(mi.getDuration() / 1000f);
			mf.setBitRate(mi.getBitRate() < 0 ? (int) (mf.getFileSize()
					* BYTEBIT / kbit / mf.getDuration()) : mi.getBitRate());
			mf.setFormat(mi.hasFormat(format) ? format : getSimpleFormat(mi
					.getFormat()));

			VideoInfo vi = mi.getVideo();
			if (null != vi)
			{
				mf.setVideoBitRate(vi.getBitRate());
				String decoder = vi.getDecoder() == null ? "" : vi.getDecoder();
				if (decoder.indexOf("(") > 0) 
					decoder = decoder.substring(0,decoder.indexOf("(")) ;
				mf.setVideoDecoder((decoder.length() > 30 ? decoder.substring(0,30) : decoder).trim());
				mf.setVideoFrameRate(vi.getFrameRate());
				VideoSize vs = vi.getSize();
				if (null != vs)
				{
					mf.setVideoSize(String.format("%d*%d", vs.getWidth(), vs
							.getHeight()));
					mf.setVideoHeight(String.format("", vs.getHeight()));
				}
			}
			AudioInfo ai = mi.getAudio();
			if (null != ai)
			{
				mf.setAudioBitRate(ai.getBitRate());
				mf.setAudioChannels(ai.getChannels());
				String decoder = ai.getDecoder() == null ? "" : ai.getDecoder();
				if (decoder.indexOf("(") > 0) 
					decoder = decoder.substring(0,decoder.indexOf("(")) ;
				mf.setAudioDecoder((decoder.length() > 30 ? decoder.substring(0,30) : decoder).trim());
				mf.setAudioSamplingRate(ai.getSamplingRate());
			}
		} catch (InputFormatException e)
		{
			throw new MamException(
					"it.sauronsoftware.jave.InputFormatException", e);
		} catch (EncoderException e)
		{
			throw new MamException("it.sauronsoftware.jave.EncoderException", e);
		}
	}

	public String getFfmpegPath()
	{
		return ffmpegPath;
	}

	public void setFfmpegPath(String ffmpegPath)
	{
		this.ffmpegPath = ffmpegPath;
	}

	public int getKbit()
	{
		return kbit;
	}

	public void setKbit(int kbit)
	{
		this.kbit = kbit;
	}

	public Map<String, String> getFormatMap()
	{
		return formatMap;
	}

	public void setFormatMap(Map<String, String> formatMap)
	{
		this.formatMap = formatMap;
	}
}
