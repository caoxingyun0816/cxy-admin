package com.wondertek.mam.util.others;

import java.io.File;
import java.io.IOException;

/**   
 *    
 * SysTool  
 * 系统操作工具类  
 * @author: sunyue  
 * 2012-10-17 上午08:34:31  
 *   
 * @version 1.0.0  
 *    
 */
public class SysTool {

	 public static final String WEB_INF = "WEB-INF";
	 public static String WEB_INF_PATH = "";
	/**
	 * 判断当前系统是window
	 * isWindow  
	 * @return    
	 * boolean   
	 * @exception    
	 * @since  1.0.0
	 */
	public final static boolean isWindow() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("win") != -1;
	}
	
	/**
	 * 判断当前系统是linux
	 * isLinux  
	 * @return    
	 * boolean   
	 * @exception    
	 * @since  1.0.0
	 */
	public final static boolean isLinux() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("linux") != -1;
	}
	
	/**
	 * 阻塞式执行操作系统命令
	 * execWaitFor  
	 * @param command
	 * @param envp
	 * @param dir
	 * @return
	 * @throws InterruptedException
	 * @throws IOException    
	 * boolean   
	 * @exception    
	 * @since  1.0.0
	 */
	public final static boolean execWaitFor(String command,String[] envp,File dir) throws InterruptedException, IOException {
		return Runtime.getRuntime().exec(command,envp,dir).waitFor() == 0;
	}
	
	/**
	 * 在单独的进程中执行操作系统命令
	 * exec  
	 * @param command
	 * @param envp
	 * @param dir
	 * @throws InterruptedException
	 * @throws IOException    
	 * Process   
	 * @exception    
	 * @since  1.0.0
	 */
	public final static Process exec(String command,String[] envp,File dir) throws IOException {
		return Runtime.getRuntime().exec(command,envp,dir);
	}
	
	/**
	 * 删除系统目录
	 * rmdir  
	 * @param targetDir
	 * @param envp
	 * @param dir
	 * @throws IOException    
	 * void   
	 * @exception    
	 * @since  1.0.0
	 */
	public final static void rmdir(String targetDir,String[] envp,File dir) throws IOException {
		if (isWindow()) {
			exec("cmd /c rmdir /s /q " + targetDir,envp,dir);
		} else {
			exec("rm -rf " + targetDir,envp,dir);
		}
	}
	
	/**
	 * 删除系统文件
	 * rmFile  
	 * @param targetFile
	 * @param envp
	 * @param dir
	 * @throws IOException    
	 * void   
	 * @exception    
	 * @since  1.0.0
	 */
	public final static void rmFile(String targetFile,String[] envp,File dir) throws IOException {
		if (isWindow()) {
			exec("cmd /c del " + targetFile,envp,dir);
		} else {
			exec("rm -rf " + targetFile,envp,dir);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
