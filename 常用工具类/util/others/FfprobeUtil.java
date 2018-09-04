package com.wondertek.mam.util.others;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;


/**
 * 获取音视频信息
 * @author chyx
 *
 */
public class FfprobeUtil {
	private static String ENCODING = "UTF-8";
	
	public static String exeOSCommond(String[] comm) throws AgentEngineException
	{
		Process myproc = null;
		BufferedReader buf = null ;
		StringBuffer sb = new StringBuffer();
		try
		{
			myproc = Runtime.getRuntime().exec(comm);
			try
			{
				// 等待执行完再往后执行 
				//myproc.waitFor();删除，可能会引起jvm缓冲区写满导致阻塞
				buf = new BufferedReader(new InputStreamReader(
						myproc.getInputStream(),ENCODING), 1024);

				String str = "";
				while ((str = buf.readLine()) != null)
				{
					sb.append(str);
				}
			} catch (Exception e)
			{
				throw new AgentEngineException(String.format(
						"execute command [%s] Exception!", StringUtils.join(
								comm, ' ')), e);
			}
		} catch (IOException e)
		{
			throw new AgentEngineException(String.format(
					"execute command [%s] Exception!", StringUtils.join(comm,
							' ')), e);
		}
		finally
		{
			if (null != buf)
				try
				{
					buf.close();
				} catch (IOException e)
				{
				}
			if(null != myproc)
				myproc.destroy();
		}
		
		return sb.toString();
	}
	
	//ffprobe -v quiet -print_format json -show_format -show_streams 
	public static String ffprobeJson(String ffprobePath, String path) {
		if(path != null) {
			String[] comm = new String[8];
			comm[0] = ffprobePath;
			comm[1] = "-v";
			comm[2] = "quiet";
			comm[3] = "-print_format";
			comm[4] = "json";
			comm[5] = "-show_format";
			comm[6] = "-show_streams";
			comm[7] = path;

			try {
				return exeOSCommond(comm);
			} catch (AgentEngineException e) {
				e.printStackTrace();
				
				return "{success:false}";
			}
		}
		return "{success:false}";
	}
}