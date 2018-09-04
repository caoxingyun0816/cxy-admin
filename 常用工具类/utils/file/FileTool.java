package com.wondertek.mam.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.wondertek.mam.util.others.AgentEngineException;
import org.apache.commons.lang.StringUtils;

/**
 * 描述：直接执行操作系统的命令
 * 
 * @author Administrator
 *
 */
public class FileTool
{
	private FileTool()
	{
	}
	private static String ENCODING = "UTF-8" ;
	private static int OS;
	static
	{
		String os = System.getProperty("os.name");
		if (os.toLowerCase().lastIndexOf("windows") >= 0)
		{
			OS = 1;
		}
		if (!StringUtils.isBlank(System.getProperty("sun.jnu.encoding")))
		{
			ENCODING = System.getProperty("sun.jnu.encoding");
		}
	}

	/**
	 * 剪切文件，直接调用操作系统的命令
	 * 
	 * @param sourceFile  	源文件
	 * @param targetPath	目的文件路径
	 * @param targetFileName	目的文件名
	 * @return 成功则返回新的文件名，失败则返回空。
	 * @throws AgentEngineException
	 */
	public static String moveFile(String sourceFile, String targetPath,
			String targetFileName) throws AgentEngineException
	{
		if (StringUtils.isBlank(targetFileName))
		{
			targetFileName = FilePathHelper.getFileNameFromPath(sourceFile);
		}
		targetPath = FilePathHelper.addSeparator(targetPath) + targetFileName  ;
		moveFile(sourceFile, targetPath) ;
		return targetFileName;
	}
	
	/**
	 * 剪切文件，直接调用操作系统的命令
	 * @param sourceFile  	源文件
	 * @param targetPath	目的文件路径
	 * @return 
	 * @throws AgentEngineException
	 */
	public static String moveFile2Dir(String sourceFile ,String targetPath ) throws AgentEngineException
	{
		return moveFile(sourceFile, targetPath, null);
	}
	/**
	 * 
	 * @param sourceFile  源文件
	 * @param targetFile  目的文件
	 * @throws AgentEngineException
	 */
	public static void moveFile(String sourceFile ,String targetFile) throws AgentEngineException
	{
		FilePathHelper.mkdirs4File(targetFile);
		
		sourceFile = StringUtils.overlay("\"", sourceFile, sourceFile
				.lastIndexOf(FilePathHelper.SEPARATOR) + 1, sourceFile
				.lastIndexOf(FilePathHelper.SEPARATOR) + 1)
				+ "\"";
		targetFile = StringUtils.overlay("\"", targetFile, targetFile
				.lastIndexOf(FilePathHelper.SEPARATOR) + 1, targetFile
				.lastIndexOf(FilePathHelper.SEPARATOR) + 1)
				+ "\"";
		
		// 组装命令
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("move /Y ").append(
					FilePathHelper.formatDir2Win(sourceFile)).append(" ")
					.append(FilePathHelper.formatDir2Win(targetFile))
					.toString();
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("mv -f ").append(sourceFile)
					.append(" ").append(targetFile).toString();
		}

		exeOSCommond(comm);
	}

	/**
	 * 给指定目录赋予每个人读和写以及执行的权限
	 * @param path
	 * @throws AgentEngineException
	 */
	public static void givePower(String path) throws AgentEngineException
	{
		//FilePathHelper.mkdirs(path);
		
		// 组装命令
		String[] comm = new String[3];
		comm[0] = "/bin/sh";
		comm[1] = "-c";
		comm[2] = new StringBuilder().append("chmod 777 ").append(path).toString();

		exeOSCommond(comm);
	}
	
	/**
	 * 给指定目录以及子目录赋予每个人读和写以及执行的权限
	 * @param path
	 * @throws AgentEngineException
	 */
	public static void giveAllPower(String path) throws AgentEngineException
	{
		//FilePathHelper.mkdirs(path);
		
		// 组装命令
		String[] comm = new String[3];
		comm[0] = "/bin/sh";
		comm[1] = "-c";
		comm[2] = new StringBuilder().append("chmod -R 777 ").append(path).toString();

		exeOSCommond(comm);
	}
	
	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return
	 * @throws AgentEngineException
	 */
	public static void deleteFile(String filePath) throws AgentEngineException
	{
		if (!fileIsExists(filePath))
			return ;
		// 组装命令
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("del /F \"").append(FilePathHelper.formatDir2Win(filePath) + "\"")
					.toString();
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("rm -f \"").append(filePath + "\"")
					.toString();
		}
		exeOSCommond(comm);
	}

	/**
	 * 删除目录
	 * 
	 * @param filePath
	 * @return
	 * @throws AgentEngineException 
	 */
	public static void deleteDir(String filePath) throws AgentEngineException
	{
		if (!fileIsExists(filePath))
			return ;
		// 组装命令
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("rmdir /S /Q ").append(FilePathHelper.formatDir2Win(filePath))
					.toString();
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("rm -rf ").append(filePath)
					.toString();
		}
		exeOSCommond(comm);
	}
	
	/**
	 * 拷贝文件
	 * @param src
	 * @param dest
	 * @throws AgentEngineException
	 */
	public static void copyFile(String src,String dest)throws AgentEngineException
	{
		FilePathHelper.mkdirs4File(dest);
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("copy /Y ").append("\""+FilePathHelper.formatDir2Win(src)+"\"").append(" \""+FilePathHelper.formatDir2Win(dest)+"\"")
					.toString();
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("cp -ruf ").append(" \""+src+"\" \""+dest+"\"")
					.toString();
		}
		exeOSCommond(comm);
	}
	
	/**
	 * 视频文件加关键帧
	 * @param src
	 * @param dest
	 * @throws AgentEngineException
	 */
	public static void yamdi(String srcFile,String destFile)throws AgentEngineException{
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("yamdi -i ").append(FilePathHelper.formatDir2Win(srcFile)).append(FilePathHelper.formatDir2Win(" -o "+destFile))
					.toString();
			System.out.println(comm[2]);
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("yamdi -i ").append(srcFile+" -o "+destFile)
					.toString();
		}
		exeOSCommond(comm);
	}

	/**
	 * 递归创建目录
	 * @param path
	 * @throws AgentEngineException
	 */
	public static void createDir(String path)throws AgentEngineException{
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("mkdir ").append(FilePathHelper.formatDir2Win(path))
					.toString();
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("mkdir -p ").append(path)
					.toString();
		}
		exeOSCommond(comm);
	}
	
	/**
	 * 执行操作系统的命令
	 * 
	 * @param comm
	 * @throws AgentEngineException
	 */
	public static void exeOSCommond(String[] comm) throws AgentEngineException
	{
		Process myproc = null;
		BufferedReader buf = null ;
		try
		{
			myproc = Runtime.getRuntime().exec(comm);
			try
			{
				// 等待执行完再往后执行
				myproc.waitFor();
				buf = new BufferedReader(new InputStreamReader(
						myproc.getErrorStream(),ENCODING));

				String str = "";
				StringBuilder sb = new StringBuilder();
				while ((str = buf.readLine()) != null)
				{
					sb.append(str);
				}

				if (StringUtils.isNotBlank(sb.toString()))
				{

					throw new AgentEngineException(String.format(
							"execute command [%s] failure! reason : %s",
							StringUtils.join(comm, ' '), sb.toString()));
				}
			} catch (InterruptedException e)
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
	}
	
	/**
	 * 判断目录是否存在
	 * @param filePath
	 * @return
	 */
	public static boolean fileIsExists(String filePath)
	{
		File tempFile = new File(filePath);
		return tempFile.exists();
	}
	
	/**
	 * 合并视频文件
	 */
	public static void mergeFile(ArrayList<String> src, String dest)
	{
		String[] comm = new String[3];
		if (OS == 1)
		{
			File file = new File(FilePathHelper.formatDir2Win(dest));
			String dir = file.getParent();
			File tempdir = new File(dir);
			if(!tempdir.exists()){
				tempdir.mkdirs();
			}
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			StringBuilder sb = new StringBuilder();
			sb.append("copy /b ");
			for(String cmd : src){
				sb.append(FilePathHelper.formatDir2Win(cmd)).append("+");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			sb.append(" " + FilePathHelper.formatDir2Win(dest));
			comm[2] = sb.toString();
			System.out.println(comm[2]);
		} else
		{
			File file = new File(dest);
			String dir = file.getParent();
			File tempdir = new File(dir);
			if(!tempdir.exists()){
				tempdir.mkdirs();
			}
			
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			
			StringBuilder sb = new StringBuilder();
			sb.append("cat ");
			for(String cmd : src){
				sb.append(cmd).append(" ");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			sb.append(" > " + dest);
			comm[2] = sb.toString();
			
		}
		try {
			exeOSCommond(comm);
		} catch (AgentEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 设备检测
	 * @param ip
	 * @throws AgentEngineException
	 */
	public static void ping(String ip)throws AgentEngineException{
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("ping -n 4 ").append(ip).toString();
			System.out.println(comm[2]);
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("ping -c 4 ").append(ip).toString();
		}
		exeOSCommond(comm);
	}
	
	/**
	 * 流化视频文件
	 * @param cmdPath
	 * @param filePath
	 * @throws AgentEngineException
	 */
	public static void hintFile(String cmdPath,String filePath,String output)throws AgentEngineException{
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append(FilePathHelper.formatDir2Win(cmdPath)).append(" -hint " + FilePathHelper.formatDir2Win(filePath)).append(" -out " + FilePathHelper.formatDir2Win(output))
					.toString();
			System.out.println(comm[2]);
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append(cmdPath).append(" -hint " + filePath +" -out " + output)
					.toString();
			System.out.println(comm[2]);
		}
		exeOSCommond(comm);
	}
	
	/**
	 * MP4box合并视频文件
	 * @param cmdPath
	 * @param filePath
	 * @throws AgentEngineException
	 */
	public static void mp4boxMergeFile(String cmdPath,List<String> filePaths,String output)throws AgentEngineException{
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			StringBuilder sb = new StringBuilder();
			sb.append(FilePathHelper.formatDir2Win(cmdPath));
			for(String filePath: filePaths){
				sb.append(" -cat ").append(FilePathHelper.formatDir2Win(filePath));
			}
			sb.append(" " + FilePathHelper.formatDir2Win(output));
			comm[2] = sb.toString();
			System.out.println(comm[2]);
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			StringBuilder sb = new StringBuilder();
			sb.append(cmdPath);
			for(String filePath: filePaths){
				sb.append(" -cat ").append(filePath);
			}
			sb.append(" " + output);
			comm[2] = sb.toString();
			System.out.println(comm[2]);
		}
		exeOSCommond(comm);
	}
	
	/**
	 * 
	 * @param sourceFile  源文件
	 * @param targetFile  目的文件
	 * @throws AgentEngineException
	 */
	public static void reNameFile(String sourceFile ,String targetFile) throws AgentEngineException
	{
		// 组装命令
		String[] comm = new String[3];
		if (OS == 1)
		{
			comm[0] = "cmd.exe";
			comm[1] = "/c";
			comm[2] = new StringBuilder().append("rename ").append(
					FilePathHelper.formatDir2Win(sourceFile)).append(" ")
					.append(FilePathHelper.formatDir2Win(targetFile))
					.toString();
		} else
		{
			comm[0] = "/bin/sh";
			comm[1] = "-c";
			comm[2] = new StringBuilder().append("mv ").append(sourceFile)
					.append(" ").append(targetFile).toString();
		}

		exeOSCommond(comm);
	}
	
	/**
	 * 备份文件
	 * @param filePath
	 * @param localBakPath
	 */
	public static void bakupFile(String filePath, String localBakPath){
		File bakDir = new File(localBakPath);
		if (!bakDir.exists()) {
			bakDir.mkdir();
		}
		File rsp = new File(filePath);
		//在本地备份下载成功的回执文件在BACKUPMETADATARESULT目录下
		try {
			FileTool.moveFile(filePath, localBakPath,
					rsp.getName());
		} catch (AgentEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 递归删除文件及文件夹
	 * @param file
	 */
	public static void deletAllFile(File file){
		if(file.isFile() || file.list().length == 0){
			file.delete();
		}else{
			File[] files = file.listFiles();
			for(File f: files){
				deletAllFile(f);
			}
			file.delete();
		}
	}
	public static void main(String[] args){
		List<String> list = new ArrayList<String>();
		list.add("F:/media/tvs/tvs_001.mp4");
		list.add("F:/media/tvs/tvs2_002.mp4");
		try {
			//mp4boxMergeFile("F:/ffmpeg0.9/bin/MP4Box.exe", list, "F:/mamstore/cptemp/699999/enl/20120828112035448.mp4");
			String path = "F:/storage/oms/content_image/programpic/1216";
			FileTool.deleteDir(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
