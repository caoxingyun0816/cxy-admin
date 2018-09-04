package com.wondertek.mam.util.temp;

/**
 * Simple to Introduction
 *
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [一句话描述该类的功能]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @Since [${date} ${time}]
 * @UpdateUser: [${user}]
 * @UpdateDate: [${date} ${time}]
 * @UpdateRemark: [说明本次修改内容]
 * @Version: [v1.0]
 */
        import java.io.*;

        import java.util.Enumeration;
        import java.util.zip.CRC32;
        import java.util.zip.CheckedOutputStream;

        import org.apache.log4j.Logger;
        import org.apache.tools.zip.ZipEntry;
        import org.apache.tools.zip.ZipFile;
        import org.apache.tools.zip.ZipOutputStream;


/**
 *
 * @create 2009-7-25 下午06:17:33
 * @since
 */
public class ZipUtils {
    private static final int BUFFER = 2048;
    private final static Logger logger = Logger.getLogger(ZipUtils.class);


    public static final String FILE_SEPARATOR = System
            .getProperty("file.separator");



    /**
     * 将指定的文件解压缩到指定的文件夹，解压后的文件夹目录和给定的压缩文件名相同.
     *
     * @param zipFilePath
     *            全路径
     * @param unZipDirectory
     *            全路径
     * @return 解压缩文件是否成功.
     * @throws IOException
     */
    public static boolean unZipFile(String zipFilePath, String unZipDirectory)
            throws IOException {
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration<?> entries = zipFile.getEntries();
        if (zipFile == null) {
            return false;
        }
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            File f = new File(unZipDirectory + FILE_SEPARATOR
                    + zipEntry.getName());
            if (zipEntry.isDirectory())
            {
                if (!f.exists() && !f.mkdirs())
                    throw new IOException("Couldn't create directory: " + f);
            } else {
                BufferedInputStream is = null;
                BufferedOutputStream os = null;
                try {
                    is = new BufferedInputStream(zipFile
                            .getInputStream(zipEntry));
                    File destDir = f.getParentFile();
                    if (!destDir.exists() && !destDir.mkdirs()) {
                        throw new IOException("Couldn't create dir " + destDir);
                    }
                    os = new BufferedOutputStream(new FileOutputStream(f));
                    int b = -1;
                    while ((b = is.read()) != -1) {
                        os.write(b);
                    }
                } finally {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                }
            }
        }
        zipFile.close();
        return true;
    }


    /**
     *  压缩一个文件
     * @param filePath
     * @param zipPath
     * @return
     */
    public static boolean zipFile(String filePath,String zipPath){
        BufferedReader in=null;
        org.apache.tools.zip.ZipOutputStream out=null;
        try{
            File file=new File(filePath);
            in=new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"ISO-8859-1"));
            FileOutputStream f=new FileOutputStream(zipPath);
            CheckedOutputStream ch=new CheckedOutputStream(f,new CRC32());
            out=new org.apache.tools.zip.ZipOutputStream(new BufferedOutputStream(ch));

            int c;
            out.putNextEntry(new org.apache.tools.zip.ZipEntry(file.getName()));
            while((c=in.read())!=-1)
                out.write(c);
        }

        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        finally{
            try {
                if(in!=null) in.close();
                if(out!=null)  out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * 压缩一个目录
     * @param dir
     * @param zipPath
     * @return
     */
    public static boolean zipDirectory(String dir,String zipPath ){
        org.apache.tools.zip.ZipOutputStream out=null;
        try{
            File dirFile=new File(dir);
            if(!dirFile.isDirectory())return false;
            FileOutputStream fo=new FileOutputStream(zipPath);
            CheckedOutputStream ch=new CheckedOutputStream(fo,new CRC32());
            out=new org.apache.tools.zip.ZipOutputStream(new BufferedOutputStream(ch));
            zip(out,dirFile,"");

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        finally{
            try {
                if(out!=null)  out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void zip(org.apache.tools.zip.ZipOutputStream out,File f,String base)throws Exception{
//		System.out.println("Zipping  "+f.getName());
        if (f.isDirectory()) {
            File[] fl=f.listFiles();
            out.putNextEntry(new org.apache.tools.zip.ZipEntry(base+"/"));
            base=base.length()==0?"":base+"/";
            for (int i=0;i<fl.length ;i++ )	{
                zip(out,fl[i],base+fl[i].getName());
            }
        }
        else {
            out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
            FileInputStream is=new FileInputStream(f);
            BufferedInputStream in = new BufferedInputStream(is);//修改BUG!二进制输出采用buffered
            int b;
            while ((b=in.read()) != -1)
                out.write(b);
            in.close();
        }

    }


    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名. 注意：该解压后的目录，已经去掉了其根目录
     *
     * @param baseDir
     *            指定根目录
     * @param absFileName
     *            相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName)
    {
        String[] dirs = absFileName.split("/");

        File ret = new File(baseDir);
        if (dirs.length > 1)
        {
            for (int i = 1; i < dirs.length - 1; i++)
            {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists())
        {
            ret.mkdirs();
        }
        // logger.info("real name is : " + ret);
        ret = new File(ret, dirs[dirs.length - 1]);
        return ret;
    }

    /**
     * 将文件夹filesFolder的内容打包到zipFilePath路径,绝对路径
     *
     * @param filesFolder
     * @param zipFilePath
     * @return zipFile
     * @author lihuan
     * @create 2010-10-19 下午07:05:16
     */
    public static File getCompressToZip(String filesFolder,String zipFilePath,String zipFileName){
        File baseFolder = new File(filesFolder);
        ZipOutputStream zos = null;
        String _zipFileName = zipFileName + ".zip";
        File zipFile = new File(zipFilePath, _zipFileName);
        try {
            if (!zipFile.exists()) {
                File parentfolder = zipFile.getParentFile();
                if (!parentfolder.exists())
                    parentfolder.mkdirs();
                zipFile.createNewFile();
            }

            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            // 将文件夹压缩为的zip文件, 去掉第一层目录
            File[] listFiles = baseFolder.listFiles();
            int listFilesSize = listFiles.length;
            for (int i = 0; i < listFilesSize; i++) {
                zipFiles(zos, listFiles[i], "");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zos != null) {
                try {
                    zos.flush();
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return zipFile;
    }

    /**
     * 定义压缩文件及目录为zip文件的方法
     *
     * @author lihuan
     * @param zos
     * @param file
     * @throws Exception
     */
    public static void zipFiles(ZipOutputStream zos, File file, String baseDir) {
        FileInputStream in = null;
        String fileName = file.getName();
        try {
            // 判断File是否为目录
            if (file.isDirectory()) {
                // 获取file目录下所有文件及目录,作为一个File数组返回
                File[] files = file.listFiles();
                baseDir =  baseDir.length() == 0 ? "" : baseDir;
                String entryName = baseDir + fileName + "/";
                zos.putNextEntry(new ZipEntry(entryName));
                int subFileSize = files.length;
                for (int i = 0; i < subFileSize; i++) {
                    zipFiles(zos, files[i], entryName);
                }
            } else {
                zos.putNextEntry(new ZipEntry(baseDir + fileName));
                in = new FileInputStream(file);
                int lenth = 0;
                int buffSize = 1024;
                byte[] buff = new byte[buffSize];
                while ((lenth = in.read(buff, 0, buffSize)) != -1) {
                    zos.write(buff, 0, lenth);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }




    public static void main(String[] args){
        //  boolean f=zipFile("e:/red100.txt","e:/red.zip");
        //boolean f=zipDirectory("e:/red","e:/red2.zip");
        try {
            unZipFile("D:\\portal_rtb.zip", "D:\\wzb");
            //zipDirectory("F:\\list","F:\\list.zip");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}

