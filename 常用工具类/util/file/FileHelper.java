package com.wondertek.mam.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.wondertek.mam.util.others.AgentEngineException;
import com.wondertek.mam.util.others.Path;
import org.apache.commons.lang.StringUtils;

/**
 * 文件操作的帮助类
 * 
 * @author ljchen
 * 
 */
public class FileHelper
{
    private static final String ENCODING = "UTF-8";

    /**
     * 计划每次从流中读取数据的长度
     */
    private final static int TO_READ_LENGTH = 10000;

    private FileHelper()
    {
    }
    
    public static InputStream getInputStream8Cls(String fileName)
    {
    	try
		{
    		return new FileInputStream(fileName); 	//按绝对路径找
		} catch (FileNotFoundException e)
		{
			try
			{
				return new FileInputStream(Path.getClassPathFromClass(FileHelper.class) + addHeadSeparator(fileName)); 	//从classpath下面找
			} catch (FileNotFoundException e1)
			{
				try
				{
					return new FileInputStream(Path.getFullPathRelateClass(fileName, FileHelper.class));	//从class的相对路径下找
				} catch (FileNotFoundException e2)
				{
					return FileHelper.class.getResourceAsStream(fileName);		//从classLoader中读取
				}
			}
		}
    }
    /**
     * 读取属性文件终的配置
     * 
     * @param fileName
     * @return
     */
    public static Properties getProperties(String fileName) throws AgentEngineException
    {
        File file = new File(fileName);
        Properties properties = new Properties();
        FileInputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);

            properties.load(inputStream);
        }
        catch (IOException e)
        {
            throw new AgentEngineException("Read Properties data from " + fileName
                    + " catch a IOException, exception message : "
                    + e.getMessage(), e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return properties;
    }

    /**
     * 
     * @param in
     *            输入流
     * @param len
     *            长度
     * @return
     * @throws AgentEngineException
     */
    public static String readFromStream(InputStream in, int len)
            throws AgentEngineException
    {
        String temp = null;
        try
        {
            if (len > 0)
            {
                byte[] b = new byte[len];
                int off = in.read(b);
                int location = off;
                while (location < len && off > 0)
                {
                    off = in.read(b, location, len - location);
                    if (off < 0)
                    {
                        break;
                    }
                    location += off;
                }
                temp = new String(b, ENCODING);
            }
        }
        catch (Exception ex)
        {
            throw new AgentEngineException(
                    "Read data from InputStream , catch a Exception , exception message : "
                            + ex.getMessage(), ex);
        }
        return temp;
    }

    /**
     * 
     * @param in
     *            输入流
     * @return
     * @throws AgentEngineException
     */
    public static String readFromStream(InputStream in) throws AgentEngineException
    {
        List<byte[]> listResult;
        int length = 0;

        // 每次成功读到的字节长度
        int readLength = 0;

        BufferedInputStream bis = null;

        // 每次成功从流中读取到的字节数组
        byte[] readByte = null;

        try
        {
            bis = new BufferedInputStream(in);
            // 计划每次从流中读取数据的字节数组
            byte[] toReadByte = new byte[TO_READ_LENGTH];

            listResult = new ArrayList<byte[]>();

            int available = bis.available();

            while ((readLength = bis.read(toReadByte, 0, TO_READ_LENGTH)) != -1)
            {
                readByte = new byte[readLength];

                System.arraycopy(toReadByte, 0, readByte, 0, readLength);

                listResult.add(readByte);

                length += readByte.length;

                if (available >= 0 && bis.available() <= 0)
                    break;
            }
        }
        catch (Exception ex)
        {
            throw new AgentEngineException(
                    "Read data from InputStream , catch a Exception , exception message : "
                            + ex.getMessage(), ex);
        }

        return transListToArray(length, listResult);
    }

    /**
     * 
     * @param in
     *            输入流
     * @return
     * @throws AgentEngineException
     */
    public static String readFromUStream(InputStream in) throws AgentEngineException
    {
        List<byte[]> listResult;
        int length = 0;

        // 每次成功读到的字节长度
        int readLength = 0;

        BufferedInputStream bis = null;

        // 每次成功从流中读取到的字节数组
        byte[] readByte = null;

        try
        {
            bis = new BufferedInputStream(in);
            // 计划每次从流中读取数据的字节数组
            byte[] toReadByte = new byte[TO_READ_LENGTH];

            listResult = new ArrayList<byte[]>();

            while ((readLength = bis.read(toReadByte, 0, TO_READ_LENGTH)) != -1)
            {
                readByte = new byte[readLength];

                System.arraycopy(toReadByte, 0, readByte, 0, readLength);

                listResult.add(readByte);

                length += readByte.length;
            }
        }
        catch (Exception ex)
        {
            throw new AgentEngineException(
                    "Read data from InputStream , catch a Exception , exception message : "
                            + ex.getMessage(), ex);
        }

        return transListToArray(length, listResult);
    }

    /**
     * 将HTTP服务器返回结果转换为字节数组形式
     * 
     * @return HTTP服务器返回结果（字节数组形式）
     * 
     * 
     * 
     * 
     */
    private static String transListToArray(int length, List<byte[]> listResult)
    {
        if (length == 0 || listResult == null || listResult.size() == 0)
        {
            return null;
        }

        byte[] byteResult = new byte[length];

        int i = 0;

        for (Iterator<byte[]> iter = listResult.iterator(); iter.hasNext();)
        {
            byte[] data = iter.next();
            int dataLength = data.length;

            System.arraycopy(data, 0, byteResult, i, dataLength);
            i = i + dataLength;
        }

        if (byteResult == null)
        {
            return null;
        }

        try
        {
            return new String(byteResult, ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }

    /**
     * 
     * @param in
     *            输入流
     * @param len
     *            长度
     * @param isMark
     *            是否mark
     * @return
     * @throws AgentEngineException
     */
    public static String readFromStream(InputStream in, int len, boolean isMark)
            throws AgentEngineException
    {
        String temp = null;
        try
        {
            if (isMark)
            {
                in.mark(0);
                if (!in.markSupported())
                {
                    throw new AgentEngineException(
                            "Read data from InputStream , don't support mark method ! ");
                }
            }

            if (len > 0)
            {
                byte[] b = new byte[len];
                int off = in.read(b);
                int location = off;
                while (location < len && off > 0)
                {
                    off = in.read(b, location, len - location);
                    if (off < 0)
                    {
                        break;
                    }
                    location += off;
                }
                temp = new String(b, ENCODING);
            }

            if (isMark)
                in.reset();
        }
        catch (Exception ex)
        {
            throw new AgentEngineException(
                    "Read data from InputStream , catch a Exception , exception message : "
                            + ex.getMessage(), ex);
        }
        return temp;
    }

    /**
     * 读取属性文件终的配置
     * 
     * @param fileName
     * @return
     */
    public static Properties getProperties(InputStream inputStream)
            throws IOException
    {
        Properties properties = new Properties();
        try
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return properties;
    }

    /**
     * 在文件路径后面加上文件路径分割符
     * 
     * @param directory
     *            F:/test
     * @return F:/test/
     */
    public static String addSeparator(String directory)
    {
        if (null == directory)
        {
            return directory;
        }

        StringBuffer result = new StringBuffer(directory);

        if (!directory.endsWith("/") && !directory.endsWith("\\"))
        {
            result.append("/");
        }
        return result.toString();
    }

    /**
     * 去掉文件路径后面的文件路径分割符
     * 
     * @param directory
     *            F:/test
     * @return F:/test/
     */
    public static String delSeparator(String directory)
    {
        if (null == directory)
        {
            return directory;
        }
        else if (directory.endsWith("/") || directory.endsWith("\\"))
        {
            directory = directory.substring(0, directory.length() - 1);
        }
        return directory;
    }

    /**
     * 在文件路径的前面加上文件路径分割符，主要用于相对路径变成绝对路径
     * 
     * @param directory
     *            String home/ccs800或者
     * @return String /home/ccs800
     */
    public static String addHeadSeparator(String directory)
    {
        String result = directory;
        if (null == directory)
        {
            return directory;
        }
        else if (!directory.startsWith("/") && !directory.startsWith("\\"))
        {
            result = "/" + directory;
        }
        return result;
    }

    /**
     * 去掉前面的盘符
     * 
     * @param directory
     *            String home/ccs800或者
     * @return String /home/ccs800
     */
    public static String delHeadSeparator(String directory)
    {
        String result = directory;
        if (null == directory)
        {
            return directory;
        }
        else if (directory.startsWith("/") || directory.startsWith("\\"))
        {
            result = result.substring(1);
        }
        return result;
    }

    /**
     * 如果目录不存在就创建目录
     * 
     * @param path
     */
    public static void mkdirs(String path)
    {
        File f = new File(path);

        String fs = f.getParent();

        f = new File(fs);

        if (!f.exists())
        {
            f.mkdirs();
        }
    }
    
    /**
     * 如果目录不存在就创建目录
     * 
     * @param path
     */
    public static void mkPath(String path)
    {
        File f = new File(path);

        if (!f.exists())
        {
            f.mkdirs();
        }
    }

    /**
     * 将date中的数据写入txt文件中
     * 
     * @param path
     *            文件路径 如："F:\beijing\cx.txt"，必须有.txt结尾，否则会失败
     * @param data
     *            写入文件中的数据
     * @param delimite
     *            分隔符 如："|"
     * @param isAppend
     *            文件运行在后面追加,true为可以
     * @param isEnd
     *            一行最后是否要跟分割符
     * @return 写入是否成功
     * @throws IOException
     * @author lizhi HT02875 2005-10-13
     */
    public static boolean createTxtFile(String path, Object[][] data,
            String delimite, boolean isAppend, boolean isEnd)
            throws IOException
    {
        // 如果目录不存在就创建目录
        mkdirs(path);

        FileWriter out = null;
        BufferedWriter buffer = null;

        try
        {
            out = new FileWriter(path, isAppend);

            buffer = new BufferedWriter(out);

            for (int i = 0; i < data.length; i++)
            {
                for (int j = 0; j < data[i].length; j++)
                {
                    buffer.write(data[i][j] == null ? "" : data[i][j]
                            .toString());

                    if (isEnd || j < (data[i].length - 1))
                    {
                        buffer.write(delimite);
                    }
                }

                if (i < (data.length - 1))
                {
                    buffer.newLine();
                }
            }
            // 关闭流
            buffer.flush();
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (null != buffer)
            {
                buffer.close();
            }
            if (null != out)
            {
                out.close();
            }
        }

        return true;
    }

    /**
     * 备份文件,会删除源文件
     * 
     * @param oldPath
     *            String 源文件路径，必须是文件的路径，不能是目录的路径
     * @param newPath
     *            String 目的路径，可以是目录，也可以是文件路径
     * @param postfix
     *            String 备份文件的格式,如果为null时,保留原文件格式,如果不为空,在原来的文件格式加后缀名postfix
     * @param append
     *            boolean 是否在原文件名(包括后缀)添加后缀,true为添加,false为不添加
     * @return boolean 正确备份返回true
     * @author lizhi HT02875
     */
    public static boolean backupFileTo(String oldPath, String newPath,
            String postfix, boolean append)
    {
        File oldFile = new File(oldPath);

        if (!oldFile.isFile())
        {
            return false;
        }

        String fileName = oldFile.getName();

        File newFile = new File(newPath);

        // 若是文件
        if (newFile.isFile())
        {
            return oldFile.renameTo(newFile);
        }
        else
        {
            String newFinePath = addSeparator(newPath);

            // 创建目录
            File file = new File(newFinePath);
            file.mkdirs();

            if (postfix != null)
            {
                if (!postfix.startsWith("."))
                {
                    postfix = "." + postfix;
                }
            }

            // 备份时保留原文件名(包括后缀)
            if (append == false && postfix == null)
            {
                newFinePath += fileName;
            }

            // 备份时修改原文件名的后缀
            else if (append == false && postfix != null)
            {
                newFinePath += fileName.substring(0, oldPath.lastIndexOf("."));
                newFinePath += postfix;

            }

            // 备份时在原文件名后面(包括后缀)多添加一个后缀
            else if (append == true && postfix != null)
            {
                newFinePath += fileName;
                newFinePath += postfix;
            }
            // 保留原文件名
            else
            {
                newFinePath += fileName;
            }

            File tempNewFile = new File(newFinePath);
            if (tempNewFile.exists())
            {
                tempNewFile.delete();
            }
            return oldFile.renameTo(tempNewFile);
        }
    }

    /**
     * 备份文件
     * 
     * @param oldPath
     *            String 源文件路径，必须是文件的路径，不能是目录的路径
     * @param newPath
     *            String 目的路径，可以是目录，也可以是文件路径
     * @param postfix
     *            String 备份文件的格式,如果为null时,保留原文件格式,如果不为空,在原来的文件格式加后缀名postfix
     * @param append
     *            boolean 是否在原文件名(包括后缀)添加后缀,true为添加,false为不添加
     * @return boolean 正确备份返回true
     * @author lizhi HT02875
     */
    public static void copyFileToDir(String oldPath, String newPath,
            String postfix, boolean append) throws IOException
    {
        File oldFile = new File(oldPath);

        if (!oldFile.isFile())
        {
            return;
        }

        String fileName = oldFile.getName();

        File newFile = new File(newPath);

        // 若是文件
        if (newFile.isFile())
        {
            copyFileToFile(oldPath, newPath);
        }
        else
        {
            String newFinePath = addSeparator(newPath);

            // 创建目录
            File file = new File(newFinePath);
            file.mkdirs();

            if (postfix != null)
            {
                if (!postfix.startsWith("."))
                {
                    postfix = "." + postfix;
                }
            }

            // 备份时保留原文件名(包括后缀)
            if (append == false && postfix == null)
            {
                newFinePath += fileName;
            }

            // 备份时修改原文件名的后缀
            else if (append == false && postfix != null)
            {
                newFinePath += fileName.substring(0, oldPath.lastIndexOf("."));
                newFinePath += postfix;

            }

            // 备份时在原文件名后面(包括后缀)多添加一个后缀
            else if (append == true && postfix != null)
            {
                newFinePath += fileName;
                newFinePath += postfix;
            }
            // 保留原文件名
            else
            {
                newFinePath += fileName;
            }

            File tempNewFile = new File(newFinePath);
            if (tempNewFile.exists())
            {
                tempNewFile.delete();
            }
            copyFileToFile(oldPath, newFinePath);
        }
    }

    /**
     * 
     * @param fileSrcPath
     *            源文件名,带路径
     * @param fileDestPath
     *            目标文件名,带路径
     */
    public static void copyFileToFile(String fileSrcPath, String fileDestPath)
            throws IOException
    {
        // 开始备份
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try
        {
            fis = new FileInputStream(fileSrcPath);
            fos = new FileOutputStream(fileDestPath);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1)
            {
                fos.write(buf, 0, i);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    /**
     * 将一个文件拷贝到另外一个目录下面进行备份
     * 
     * @param fileSrcPath
     *            源文件的路径 如 f:\srcfile\beijing.txt
     * @param fileDestDir
     *            备份到哪个目录下面，必须是路径 如 f:\back
     * 
     * @throws IOException
     *             文件操作失败（可能是没有源文件等）
     */
    public static void copyFileToDir(String fileSrcPath, String fileDestDir)
            throws IOException
    {
        fileDestDir = addSeparator(fileDestDir);

        String fileName = new File(fileSrcPath).getName();

        File file = new File(fileDestDir);
        file.mkdirs();

        // 备份路径
        String fileDestPath = fileDestDir + fileName;

        copyFileToFile(fileSrcPath, fileDestPath);
    }

    /**
     * 删除指定文件
     * 
     * @param filePath
     *            待删除的文件
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath)
    {
        File file = new File(filePath);
        // 不存在此目录
        if (!file.exists())
        {
            return false;
        }

        // 不是目录
        if (!file.isFile())
        {
            return false;
        }

        return file.delete();
    }

    public static List<String> readText(String fileName) throws IOException
    {
        FileReader in = null;
        BufferedReader buffer = null;
        try
        {
            in = new FileReader(fileName);
            buffer = new BufferedReader(in);
            List<String> list = new ArrayList<String>();
            String row;
            while ((row = buffer.readLine()) != null)
            {
                list.add(row.trim());
            }
            return list;
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (buffer != null)
            {
                try
                {
                    buffer.close();
                }
                catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    public static List<String> readText(String fileName, int begineNum,
            int endNum) throws IOException
    {
        FileReader in = null;
        BufferedReader buffer = null;
        try
        {
            in = new FileReader(fileName);
            buffer = new BufferedReader(in);
            List<String> list = new ArrayList<String>();
            String row;
            int idx = 0;
            while ((row = buffer.readLine()) != null)
            {
                idx++;
                if (idx < begineNum)
                    continue;
                if (idx > endNum)
                    break;
                list.add(row.trim());
            }
            return list;
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (buffer != null)
            {
                try
                {
                    buffer.close();
                }
                catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    /**
     * 解析txt文件
     * 
     * @param file
     * @param delimite
     * @param list
     * @throws IOException
     */
    public static void readText(File file, String delimite, List<String[]> list)
            throws IOException
    {
        FileReader in = null;
        BufferedReader buffer = null;
        try
        {
            in = new FileReader(file);
            buffer = new BufferedReader(in);
            String row;
            while ((row = buffer.readLine()) != null)
            {
                list.add(row.trim().split(delimite));
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (buffer != null)
            {
                try
                {
                    buffer.close();
                }
                catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    /**
     * 从文件路径中获得文件名字
     * 
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath)
    {
        if (null == filePath || "".equals(filePath))
            return filePath;

        int idx = filePath.lastIndexOf("/");

        idx = idx > filePath.lastIndexOf("\\") ? idx : filePath
                .lastIndexOf("\\");

        if (idx > 0)
        {
            return filePath.substring(idx);
        }
        else
        {
            return filePath;
        }
    }
    
    /**
     * 写数据到txt文件中
     * @param datas 数据
     * @param filePath 文件路径含名字
     * @param newLine 换行符
     * @throws AgentEngineException
     */
    public static void writeDatas2Txt(List<String> datas, String filePath , String newLine) throws AgentEngineException
    {
        mkdirs(filePath);
        
        FileWriter out = null;
        BufferedWriter bufferW = null;
        try
        {

            out = new FileWriter(filePath);
            bufferW = new BufferedWriter(out);

            for (String line : datas)
            {
                bufferW.write(line);
                if (null == newLine)
                {
                    bufferW.newLine();
                }else {
                    bufferW.write(newLine);
                }
            }
            bufferW.flush();
        }
        catch (Exception e)
        {
            throw new AgentEngineException(e);
        }
        finally
        {
            if (null != bufferW)
            {
                try
                {
                    bufferW.close();
                }
                catch (IOException e)
                {
                }
            }
            if (null != out)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }
    
    /**
     * 过滤文件
     * 
     * @param file
     * @return
     */
    public static boolean filterFile(final String fileFilter, String fileName)
    {
        if (fileFilter == null || fileFilter.length() <= 0)
        {
            return true;
        }

        String regex = fileFilter.replaceAll("[*]", "[\\\\w|\\\\W]+");
        
        regex = regex.replaceAll("[?]", "[\\\\w|\\\\W]");

        return fileName.matches(regex);
    }
    
    /**
	 * 连接文件路径,只起到连接作用，目录两头不做盘符的修改
	 * 
	 * @param params
	 * @return a/b/c/ or /a/b/c/
	 */
	public static String joinPath(String... params)
	{
		if (params.length == 0)
			return "";
		String path = "";
		for (int i = 0; i < params.length; i++)
		{
			if (StringUtils.isBlank(params[i]))
				continue;
			if (StringUtils.isBlank(path))
				path = delSeparator(params[i]);
			else if (i < params.length - 1)
				path += addHeadSeparator(delSeparator(params[i]));
			else
				path += addHeadSeparator(params[i]);
		}
		return path;
	}
	
	public static void main(String[] args)
	{
		try
		{
			List<String> rows = FileHelper.readText("e:/soap.log");
			List<String> inList = new ArrayList<String>();
			
			for (String row : rows)
			{
				String tmp = row = row.substring(row.indexOf("logid = ") + 8);
				tmp = tmp.substring(0 , tmp.indexOf(";"));
				inList.add(tmp);
			}
			
			Collections.sort(inList);
			for (String row : inList)
			{
				System.out.println(row);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
