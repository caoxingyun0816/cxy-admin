/**
 * 
 */
package com.wondertek.mam.util.superutil.thirdparty.http;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author Goofy 
 * Http下载工具类
 */
public class HttpServletUtils {
	private static final Logger log=Logger.getLogger(HttpServletUtils.class);

	/**
	 * 获取项目网络路径
	 * @param request
	 * @return
	 */
	public static String getContentpath(HttpServletRequest request){
		return request.getContextPath();
	}
	
	/**
     * 获取项目磁盘绝对路径
     */
    public static String getRealPath(HttpServletRequest request){
        return request.getSession().getServletContext().getRealPath("/");
    }
    
    /**
     * 使用了代理服务器的，无法获取正确地址的，使用这个方法获取访问者的IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

	/**
	 * 下载多个文件
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param files
	 *            File
	 */
	public static void download(HttpServletResponse response, File... files) {

		for (File file : files) {
			download(response, file, file.getName());
		}

	}

	/**
	 * 下载文件
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param file
	 *            文件
	 * @param fileName
	 *            下载的输出文件名
	 */
	public static void download(HttpServletResponse response, File file, String fileName) {
		InputStream is = null;
		String _fileName = null;
		try {
			is = new FileInputStream(file);
			_fileName = fileName == null ? file.getName() : fileName;
			download(response, is, _fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param is
	 *            输入流
	 * @param fileName
	 *            文件名
	 * @param response
	 */
	public static void download(HttpServletResponse response, InputStream is, String fileName) {
		OutputStream os = null;
		try {
			response.reset();
			response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			os = response.getOutputStream();
			byte buffer[] = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
			try {
				if (os != null)
					os.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				if (is != null)
					is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 该方法有bug，请勿用
	 * @param response
	 * @param work
	 * @param fileName
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, Workbook work, String fileName) throws IOException {
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			response.setContentType("application/ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename="
					.concat(String.valueOf(URLEncoder.encode(fileName + ".xls", "UTF-8"))));
			work.write(out);
		} catch (IOException e) {
			log.info("输出流错误");
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	/**
	 * 浏览器下载文件
	 *
	 * @param srcFile  将要下载的文件File对象
	 * @param response HttpServletResponse
	 * @param request  HttpServletRequest
	 * @param fileName 指定文件下载到客户端后的文件名; 可设为null,则默认使用srcFile的文件名
	 */
	public static boolean downloadFile(File srcFile, HttpServletResponse response, HttpServletRequest request, String fileName) {
		boolean flag = false;
		if (fileName == null) {
			fileName = srcFile.getName();
		}
		try {
			flag = downloadFile(new FileInputStream(srcFile), response, request, fileName);
		} catch (FileNotFoundException e) {
			log.error("C2SFileTools.downloadFile() : 下载源文件没有找到！", e);
		}
		return flag;
	}

	/**
	 *
	 * @param request 可以为null
	 * @param response
	 * @param work
	 * @param fileName
	 * @return
	 */
	public static boolean downloadExcelFile(HttpServletRequest request, HttpServletResponse response, Workbook work, String fileName) {
		boolean flag = false;
		if (response == null || work == null) {
			return flag;
		}
		//response.setContentType("multipart/form-data");
		response.setContentType("application/ms-excel;charset=UTF-8");
		OutputStream os = null;
		try {
			if (request.getHeader("User-agent") != null && request.getHeader("User-agent").toLowerCase().contains("firefox")) {
				response.setHeader("Content-Disposition", "attachment;fileName=" + MimeUtility.encodeWord(fileName));
			} else {
				response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
			}
			os = response.getOutputStream();
		} catch (UnsupportedEncodingException e) {
			log.info("downloadFile() : 文件名解码失败，不支持的编码类型", e);
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		try {
			work.write(os);
			flag |= true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
					flag &= true;
				}
			} catch (IOException e) {
				log.info("downloadFile() : 文件输入输出流关闭失败", e);
			}
		}
		return flag;
	}

	public static boolean downloadFile(InputStream is, HttpServletResponse response, HttpServletRequest request, String fileName) {
		boolean flag = false;
		if (response == null || is == null) {
			return flag;
		}
		response.setContentType("multipart/form-data");
		try {
			if (request.getHeader("User-agent") != null && request.getHeader("User-agent").toLowerCase().contains("firefox")) {

				response.setHeader("Content-Disposition", "attachment;fileName=" + MimeUtility.encodeWord(fileName));
			} else {
				response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			log.error("C2SFileTools.downloadFile() : 文件名解码失败，不支持的编码类型", e);
		}
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			int len = 0;
			byte[] data = new byte[1024];
			while ((len = is.read(data)) != -1) {
				os.write(data, 0, len);
			}
			flag |= true;
		} catch (IOException e) {
			log.error("C2SFileTools.downloadFile() : 文件名解码失败，不支持的编码类型", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.flush();
					os.close();
					flag &= true;
				}
			} catch (IOException e) {
				log.error("C2SFileTools.downloadFile() : 文件输入输出流关闭失败", e);
			}
		}
		return flag;
	}

}
