package com.wondertek.mam.util.file;

import java.io.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 类说明: 文件操作类
 *
 * @author ysk E-mail:libra_ysk@hotmail.com
 * @version 创建时间：2008-12-8 下午04:12:54
 * @update liuping
 *
 */
public class FileUtil {
	private final static Log log = LogFactory.getLog(FileUtil.class);
	private final static byte[] UTF_8_BOM ={(byte) 0xEF,(byte) 0xBB,(byte) 0xBF};
	/**
	 * 删除指定文件或目录
	 *
	 * @param file
	 * 		需要被删除的文件
	 * @return
	 * 		0成功,非0失败
	 */
	public static int deleteFile(File file) {
		if(file == null) {
			return -1;
		}
		if(file.exists()) {
			if(file.isFile()) {
				file.delete();
			}
			if(file.isDirectory()) {
				File[] subFiles = file.listFiles();
				if(subFiles != null) {
					for(int i=0;i<subFiles.length;i++) {
						deleteFile(subFiles[i]);
					}
				}
				file.delete();
			}
			return 0;
		}else {
			return -1;
		}
	}

	/**
	 * 文件拷贝
	 * @param srcFile
	 * @param tarFile
	 */
	public static void fileCopy(File srcFile, File tarFile) throws Exception{
		FileInputStream is = null;
		FileOutputStream os = null;
		try {
			createNewFile(tarFile);
			is = new FileInputStream(srcFile);
			os = new FileOutputStream(tarFile);
			// write the file to the file specified
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				throw e;
			}
			try {
				if (os != null)
					os.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * 功能描述：拷贝一个目录或者文件到指定路径下，即把源文件拷贝到目标文件路径下
	 *
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件路径
	 * @return void
	 */
	public static void copy(File source, File target) {
		File tarpath = new File(target, source.getName());
		if (source.isDirectory()) {
			tarpath.mkdir();
			File[] dir = source.listFiles();
			for (int i = 0; i < dir.length; i++) {
				copy(dir[i], tarpath);
			}
		} else {
			try {
				InputStream is = new FileInputStream(source); // 用于读取文件的原始字节流
				OutputStream os = new FileOutputStream(tarpath); // 用于写入文件的原始字节的流
				byte[] buf = new byte[1024];// 存储读取数据的缓冲区大小
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					os.write(buf, 0, len);
				}
				is.close();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将字符串写入到文件中，
	 * @param filePath      文件路径+文件名
	 * @param fileContent   文件内容
	 * @param encoding      字符串编码格式 默认系统编码
	 * @throws Exception
	 */
	public static void writeFileByString(String filePath, String fileContent,
										 String encoding) {
		FileUtil.writeFileByString(filePath, fileContent, encoding, false);
	}


	/**
	 * 将字符串写入到文件中，
	 *
	 * @param filePath
	 *            文件路径+文件名
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            字符串编码格式 默认系统编码
	 * @param append
	 *            如果为true表示将fileContent中的内容添加到文件file末尾处
	 * @throws Exception
	 *
	 * @author duguocheng
	 */
	public static void writeFileByString(String filePath, String fileContent,
										 String encoding, boolean append) {
		PrintWriter out = null;
		try {
			if (filePath == null || fileContent == null
					|| fileContent.length() <= 0) {
				log.error("into writeFileByString [filePath=" + filePath
						+ ",filePath=" + fileContent + "] is null return!!!");
				return;
			}

			if (append) {
				File tempFile = new File(filePath);
				if (!tempFile.exists()) {
					tempFile.getParentFile().mkdirs();
					tempFile.createNewFile();
				}
			} else
				createNewFile(new File(filePath));

			if (encoding == null || encoding.trim().length() <= 0) {
				out = new PrintWriter(new FileWriter(filePath));
			} else {
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,append), encoding)), true);
			}
			out.print(fileContent);
			out.close();
		} catch (Exception e) {
			log.error("writeFileByString Exception:" + e, e);
		} finally {
			if (out != null)
				out.close();
		}
	}

	public static void writeFileByStrs(String filePath, List<String> fileContent,
									   String encoding) {
		PrintWriter out = null;
		try {
			if (filePath == null || fileContent == null
					|| fileContent.size() <= 0) {
				log.error("into writeFileByString [filePath=" + filePath
						+ ",filePath=" + fileContent + "] is null return!!!");
				return;
			}

			createNewFile(new File(filePath));

			if (encoding == null || encoding.trim().length() <= 0) {
				out = new PrintWriter(new FileWriter(filePath));
			} else {
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding)), true);
			}
			//out.print(fileContent);
			for(String content:fileContent){
				out.println(content);
			}
			out.close();
		} catch (Exception e) {
			log.error("writeFileByString Exception:" + e, e);
		} finally {
			if (out != null)
				out.close();
		}
	}


	/**
	 * 将字符串写入到文件中，
	 *
	 * @param filePath
	 *            文件路径+文件名
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            字符串编码格式 默认系统编码
	 * @param append
	 *            如果为true表示将fileContent中的内容添加到文件file末尾处
	 * @throws Exception
	 *
	 * @author duguocheng
	 * @throws Exception
	 */
	public static void writeFileToString(String filePath, String fileContent,String encoding, boolean append) throws Exception {
		PrintWriter out = null;
		try {
			if (filePath == null || fileContent == null
					|| fileContent.length() <= 0) {
				log.error("into writeFileByString [filePath=" + filePath
						+ ",filePath=" + fileContent + "] is null return!!!");
				return;
			}

			File tempFile = new File(filePath);
			if (!tempFile.exists()) {
				tempFile.getParentFile().mkdirs();
				tempFile.createNewFile();
			}

			if (encoding == null || encoding.trim().length() <= 0) {
				out = new PrintWriter(new FileWriter(filePath));
			} else {
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,append), encoding)), true);
			}
			out.print(fileContent);
			out.close();
		} catch (Exception e) {
			log.error("writeFileByString Exception:" + e, e);
			throw e;
		} finally {
			if (out != null)
				out.close();
		}
	}





	/**
	 * 将字符串写入到文件中，
	 *
	 * @param filePath
	 *            文件路径+文件名
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            字符串编码格式 默认系统编码
	 * @param append
	 *            如果为true表示将fileContent中的内容添加到文件file末尾处
	 * @throws Exception
	 *
	 * @author duguocheng
	 */
	public static void writeFileByString(String filePath, String fileContent,String encoding, boolean append,boolean isNewLine) {
		PrintWriter out = null;
		try {
			if (filePath == null || fileContent == null
					|| fileContent.length() <= 0) {
				log.error("into writeFileByString [filePath=" + filePath
						+ ",filePath=" + fileContent + "] is null return!!!");
				return;
			}

			if (append) {
				File tempFile = new File(filePath);
				if (!tempFile.exists()) {
					tempFile.getParentFile().mkdirs();
					tempFile.createNewFile();
				}
			} else
				createNewFile(new File(filePath));

			if (encoding == null || encoding.trim().length() <= 0) {
				out = new PrintWriter(new FileWriter(filePath));
			} else {
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,append), encoding)), true);
			}
			if(isNewLine){
				out.println();
			}
			out.print(fileContent);
			out.close();
		} catch (Exception e) {
			log.error("writeFileByString Exception:" + e, e);
		} finally {
			if (out != null)
				out.close();
		}
	}

	public static void writeBytesToFile(byte[] bytes, String filePath) {
		try {
			File tempFile = new File(filePath);
			if (!tempFile.exists()) {
				tempFile.getParentFile().mkdirs();
				tempFile.createNewFile();
			}
			FileChannel fc = new FileOutputStream(filePath).getChannel();
			fc.write(ByteBuffer.wrap(bytes));
			fc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String loadAFileToString(File f){
		InputStream is = null;
		String ret = null;
		try {
			is = new BufferedInputStream( new FileInputStream(f) );
			long contentLength = f.length();
			ByteArrayOutputStream outstream = new ByteArrayOutputStream( contentLength > 0 ? (int) contentLength : 1024);
			byte[] buffer = new byte[4096];
			int len;
			while ((len = is.read(buffer)) > 0) {
				outstream.write(buffer, 0, len);
			}
			outstream.close();
			ret = new String(outstream.toByteArray(), "utf-8");
		}catch(IOException e){
			ret="";
		} finally {
			if(is!=null) {try{is.close();} catch(Exception e){} }
		}
		return ret;
	}

	public static String loadAFileToString(File f,String charsetName){
		InputStream is = null;
		String ret = null;
		try {
			is = new BufferedInputStream( new FileInputStream(f) );
			long contentLength = f.length();
			ByteArrayOutputStream outstream = new ByteArrayOutputStream( contentLength > 0 ? (int) contentLength : 1024);
			byte[] buffer = new byte[4096];
			int len;
			while ((len = is.read(buffer)) > 0) {
				outstream.write(buffer, 0, len);
			}
			outstream.close();
			ret = new String(outstream.toByteArray(), charsetName);
		}catch(IOException e){
			ret="";
		} finally {
			if(is!=null) {try{is.close();} catch(Exception e){} }
		}
		return ret;
	}

	public static String loadAFileToString2(File f) {
		InputStream is = null;
		String ret = "";
		if (f.exists()) {
			BufferedReader br = null;
			try {
				String line;
				is = new FileInputStream(f);
				InputStreamReader read = new InputStreamReader(is, "utf-8");
				br = new BufferedReader(read);
				while ((line = br.readLine()) != null) {
					ret += line + "\r\n";
				}
			} catch (Exception e) {
				ret = "";
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return ret;
	}
	/**
	 * 创建文件，如果存在，删除后，新建
	 * @param f
	 * @throws IOException
	 */
	public static void createNewFile(File f)throws IOException{
		if(f.exists()){
			f.delete();
		}
		f.getParentFile().mkdirs();
		f.createNewFile();
	}

	public static boolean checkFileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}


	public static void checkDirExists(String dirPath) {
		File dirFile = new File(dirPath);
		if (!dirFile.isDirectory()) {
			dirFile.mkdirs();
		}
	}


	/**
	 * 取得文件大小单位M
	 * @param f
	 * @throws IOException
	 */
	public static double getFileSize(File f)throws IOException{
		FileInputStream fis =null;
		try {
			fis = new FileInputStream(f);
			BigDecimal b = new BigDecimal(Integer.toString(fis.available()));
			BigDecimal s = new BigDecimal("1048576");
			return b.divide(s).doubleValue();
		}catch(Exception e) {

		}finally{
			if(fis != null)
				fis.close();
		}
		return 0;
	}

	/**
	 * 判断文件是否为UTF-8格式
	 * @param f
	 * @return
	 */
	public static boolean isUTF8(File f){
		boolean isUtf8 =false;
		try {
			FileInputStream fis = new FileInputStream(f);
			byte[] bom = new byte[3];
			fis.read(bom);
			fis.close();
			if(null != bom && bom.length>2
					&&bom[0]==UTF_8_BOM[0]
					&&bom[1]==UTF_8_BOM[1]
					&&bom[2]==UTF_8_BOM[2]){
				isUtf8= true;
			}
		} catch (Exception e) {
			return false;
		}
		return isUtf8;
	}

	/**
	 * 把一个文件A追加到另一个文件B中，
	 * @param sorceFile 文件A
	 * @param despFile  文件B
	 */
	public static void writeFileByFileWithUtf(File sorceFile, File despFile) {
		long sourceLength = sorceFile.length();
		if (sourceLength == 0) {
			return;
		}

		InputStream is = null;
		FileOutputStream fos = null;

		byte[] bytes = new byte[1024 * 4];

		int numRead = 0;
		try {
			double fileSize = FileUtil.getFileSize(despFile);

			is = new FileInputStream(sorceFile);
			fos = new FileOutputStream(despFile, true);

			if (fileSize == 0) {
				byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
				fos.write(bom);
			}

			while ((numRead = is.read(bytes, 0, 1024 * 4)) >= 0) {
				if (numRead < 1024 * 4) {
					String str = new String(bytes);
					String pattern = "\\x00+$";
					Pattern p = Pattern.compile(pattern);
					Matcher m = p.matcher(str);
					str = m.replaceAll("");
					bytes = str.getBytes();
				}

				fos.write(bytes, 0, bytes.length);
				bytes = new byte[1024 * 4];
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将字符串content的内容追加到文件filePath的最后，如果文件不存在，先创建新文件
	 * 追加文件：在构造FileOutputStream时，把第二个参数设为true
	 * 编码：UTF-8
	 *
	 * @param filePath	文件路径+文件名
	 * @param content	内容
	 */
	public static boolean writeFileAppend(String filePath, String content) {
		boolean res = true;
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath, true), "UTF-8"));
			out.write(content);
		} catch (Exception e) {
			log.error(e.getMessage());
			res = false;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * 判断参数是不是文件头标识
	 * @param content
	 * @return
	 */
	public static boolean isFileHead(String content){
		byte[] b = content.trim().getBytes();
		if(b.length!=3) return false;
		if(b[0]!=-17) return false;
		if(b[1]!=-69) return false;
		if(b[2]!=-65) return false;
		return true;
	}


	/**
	 * 把一个文件A追加到另一个文件B中，
	 *
	 * @param fileName
	 *            文件A
	 * @param text
	 *            文件B
	 * @throws IOException
	 */
	public static void appendTextToFile(String fileName, String text)
			throws IOException {
		if (text.length() == 0) {
			return;
		}
		File despFile = new File(fileName);
		if (!checkFileExists(fileName)) {
			File f = new File(fileName);
			createNewFile(f);
		}
		InputStream is = null;
		FileOutputStream fos = null;

		byte[] bytes = new byte[1024 * 4];

		int numRead = 0;
		try {
			double fileSize = FileUtil.getFileSize(despFile);

			is = new ByteArrayInputStream(text.getBytes());
			fos = new FileOutputStream(despFile, true);

			if (fileSize == 0) {
				byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
				fos.write(bom);
			}

			while ((numRead = is.read(bytes, 0, 1024 * 4)) >= 0) {
				if (numRead < 1024 * 4) {
					String str = new String(bytes);
					String pattern = "\\x00+$";
					Pattern p = Pattern.compile(pattern);
					Matcher m = p.matcher(str);
					str = m.replaceAll("");
					bytes = str.getBytes();
				}

				fos.write(bytes, 0, bytes.length);
				bytes = new byte[1024 * 4];
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static String loadFileToStr(File f) throws FileNotFoundException {
		InputStream is = null;
		String ret = "";
		if (f.exists()) {
			BufferedReader br = null;
			try {
				String line;
				is = new FileInputStream(f);
				InputStreamReader read = new InputStreamReader(is, "utf-8");
				br = new BufferedReader(read);
				while ((line = br.readLine()) != null) {
					ret += line + "\r\n";
					log.debug(ret);
				}
			} catch (Exception e) {
				ret = "";
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return ret;
	}

	public static File createFile(String filepath) throws Exception {
		File file = new File(filepath);
		if (file.exists()) {// 判断文件目录的存在
			if (file.isDirectory()) {// 判断文件的存在性
			} else {
				file.createNewFile();// 创建文件
			}
		} else {
			File file2 = new File(file.getParent());
			file2.mkdirs();
			if (file.isDirectory()) {
			} else {
				file.createNewFile();// 创建文件
			}
		}
		return file;
	}

	/**
	 * 获取指定文件夹对象下面的所有文件对象,如当前对象是文件对象,则返回只包含一个文件对象的集合
	 * @param arrayList 返回的文件对象集合
	 * @param file 当前文件夹
	 * @return
	 */
	public static ArrayList<File> getFileArrayList(ArrayList<File> arrayList, File file)
	{
		log.info("Method getFileArrayList started");
		if(file.exists()&&file.isDirectory())
		{
			for(File subFile:file.listFiles())
			{
				getFileArrayList(arrayList,subFile);
			}
		}
		else
		{
			arrayList.add(file);
		}
		log.info("Method getFileArrayList ended");
		return arrayList;
	}
	/**
	 * 将文件大小（单位B）转化
	 * @param size
	 * @return
	 */
	public static  String convertSize(long size) {
		if (size < 1024)
			return size + "B";
		String fs = null, unit = "KB";
		if (size < 1024 * 1024)
			fs = String.valueOf(size * 1f / 1024);
		else {
			fs = String.valueOf(size * 1f / (1024 * 1024));
			unit = "MB";
		}
		int point = fs.indexOf(".");
		if (point == -1 || point >= fs.length() - 3)
			return fs + unit;
		return fs.substring(0, point + 2) + unit;
	}
	/**
	 * 分隔 TXT文件。
	 * @param rows //多少行分隔一个文件，越小越快
	 * @param sourceFilePath //源文件
	 * @param targetDirectoryPath//目标文件夹
	 * @return 新增文件名
	 */
	public static List<String> SegTextFile(int rows, String sourceFilePath,String targetDirectoryPath) {
		List<String> lists = new ArrayList<String>();
		if (sourceFilePath == null || targetDirectoryPath == null)
			return lists;
		if (!targetDirectoryPath.equals("") && !targetDirectoryPath.endsWith("/")) {
			targetDirectoryPath = targetDirectoryPath + "/";
		}
		File sourceFile = new File(sourceFilePath);
		File targetFile = new File(targetDirectoryPath);
		if (!sourceFile.exists() || rows <= 0 || sourceFile.isDirectory()) {
			return lists;
		}
		if (targetFile.exists()) {
			if (!targetFile.isDirectory()) {
				return lists;
			}
		} else {
			targetFile.mkdirs();
		}

		try {
			String fileName = sourceFile.getName();
			String prefixn = fileName.substring(0, fileName.lastIndexOf("."));
			String suffixn = fileName.substring(fileName.lastIndexOf("."),fileName.length());
			String targetFilePath = targetFile.getAbsolutePath()+"/";

			String name = null;
			String targetName = null;

			BufferedReader br = new BufferedReader(new FileReader(sourceFile));
			StringBuffer str = new  StringBuffer();
			String tempData = br.readLine();
			int i = 1, s = 1;
			while (tempData != null) {
				str.append(tempData).append("\r\n");
				if (i % rows == 0) {
					targetName  = prefixn+"_"+s+suffixn;
					name = targetFilePath+targetName;
					writeFileByString(name,str.toString(), "UTF-8");
					str.setLength(0);
					s += 1;
					i = 0;
					lists.add(targetName);
				}
				i++;
				tempData = br.readLine();
			}

			if (str.length() >0) {
				targetName  = prefixn+"_"+s+suffixn;
				name = targetFilePath+targetName;
				writeFileByString(name,str.toString(), "UTF-8");
				s += 1;
				lists.add(targetName);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return lists;
	}

	/**
	 * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
	 *
	 * @param res            原字符串
	 * @param filePath 文件路径
	 * @return 成功标记
	 */
	public static boolean string2File(String res, String filePath) {
		boolean flag = true;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			File distFile = new File(filePath);
			if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();
			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024];         //字符缓冲区
			int len;
			while ((len = bufferedReader.read(buf)) != -1) {
				bufferedWriter.write(buf, 0, len);
			}
			bufferedWriter.flush();
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
			return flag;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 改变txt的编码方式(ANSI转UTF-8)
	 * @param f
	 */
/*	private void change(File f){
		BufferedReader buf = null;
		OutputStreamWriter pw = null;
		String str = null;
		String allstr = "";

		//用于输入换行符的字节码
		byte[] c = new byte[2];
		c[0] = 0x0d;
		c[1] = 0x0a;
		String t = new String(c);

		try {
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(f), "GBK"));
			while((str = buf.readLine()) != null){
				allstr = allstr + str + t;
			}

			buf.close();

			pw = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			pw.write(allstr);
			pw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/



	/**
	 * 文本文件转换为指定编码的字符串
	 *
	 * @param file         文本文件
	 * @param encoding 编码类型
	 * @return 转换后的字符串
	 * @throws IOException
	 */
	public static String file2String(File file, String encoding) {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		int DEFAULT_BUFFER_SIZE = 2048;
		try {
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			} else {
				reader = new InputStreamReader(new FileInputStream(file));
			}
			//将输入流写入输出流
			char[] buffer = new char[DEFAULT_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		//返回转换结果
		if (writer != null)
			return writer.toString();
		else return null;
	}


	/**

	 * 压缩文件

	 * @param inputFileName 要压缩的文件或文件夹路径，例如：c:\\a.txt,c:\\a\

	 * @param outputFileName 输出zip文件的路径，例如：c:\\a.zip

	 */

	public static void zip(String inputFileName, String outputFileName) throws Exception {



		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFileName));

		zip(out, new File(inputFileName), "");

		log.debug("压缩完成！");

		out.closeEntry();

		out.close();

	}

	/**

	 * 压缩文件

	 * @param out org.apache.tools.zip.ZipOutputStream

	 * @param file 待压缩的文件

	 * @param base 压缩的根目录

	 */

	private static void zip(ZipOutputStream out, File file, String base) throws Exception {



		if (file.isDirectory()) {

			File[] fl = file.listFiles();

			base = base.length() == 0 ? "" : base + File.separator;

			for (int i = 0; i < fl.length; i++) {

				zip(out, fl[i], base + fl[i].getName());

			}

		} else {

			out.putNextEntry(new ZipEntry(base));

			log.debug("添加压缩文件：" + base);

			FileInputStream in = new FileInputStream(file);

			int b;

			while ((b = in.read()) != -1) {

				out.write(b);

			}

			in.close();

		}

	}



	/**

	 * 解压zip文件

	 * @param zipFileName 待解压的zip文件路径，例如：c:\\a.zip

	 * @param outputDirectory 解压目标文件夹,例如：c:\\a\

	 */

	public static void unZip(String zipFileName, String outputDirectory) throws Exception {



		ZipFile zipFile = new ZipFile(zipFileName);

		try {

			Enumeration<?> e = zipFile.getEntries();

			ZipEntry zipEntry = null;

			createDirectory(outputDirectory, "");

			while (e.hasMoreElements()) {

				zipEntry = (ZipEntry) e.nextElement();

				log.debug("解压：" + zipEntry.getName());

				if (zipEntry.isDirectory()) {

					String name = zipEntry.getName();

					name = name.substring(0, name.length() - 1);

					File f = new File(outputDirectory + File.separator + name);

					f.mkdir();

					log.debug("创建目录：" + outputDirectory + File.separator + name);

				} else {

					String fileName = zipEntry.getName();

					fileName = fileName.replace('\\', '/');

					if (fileName.indexOf("/") != -1) {

						createDirectory(outputDirectory, fileName.substring(0, fileName.lastIndexOf("/")));

						fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

					}



					File f = new File(outputDirectory + File.separator + zipEntry.getName());



					f.createNewFile();

					InputStream in = zipFile.getInputStream(zipEntry);

					FileOutputStream out = new FileOutputStream(f);



					byte[] by = new byte[1024];

					int c;

					while ((c = in.read(by)) != -1) {

						out.write(by, 0, c);

					}

					in.close();

					out.close();

				}

			}

		} catch (Exception ex) {

			System.out.println(ex.getMessage());

		} finally {

			zipFile.close();

			log.debug("解压完成！");

		}



	}

	private static void createDirectory(String directory, String subDirectory) {



		String dir[];

		File fl = new File(directory);

		try {

			if (subDirectory == "" && fl.exists() != true) {

				fl.mkdir();

			} else if (subDirectory != "") {

				dir = subDirectory.replace('\\', '/').split("/");

				for (int i = 0; i < dir.length; i++) {

					File subFile = new File(directory + File.separator + dir[i]);

					if (subFile.exists() == false)

						subFile.mkdir();

					directory += File.separator + dir[i];

				}

			}

		} catch (Exception ex) {

			System.out.println(ex.getMessage());

		}

	}

	public static void main(String[] args){
		List<String> names = SegTextFile(30000,"E:/mywork/PJ375/BK/sync/NODE_CONT_201211230300_101/NODE_CONT_201211230300_101.TXT","E:/mywork/PJ375/BK/sync/NODE_CONT_201211230300_101/NODE_CONT_201211230300_101");
		System.out.print(names.size());
	}
}
