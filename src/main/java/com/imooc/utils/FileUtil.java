package com.imooc.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by caoxingyun on 2018/8/1.
 * 文件处理类
 */
public class FileUtil {

    private Logger log = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 复制文件
     * @param souceFile
     * @param tragargetFile
     */
    public  void copyFile(String souceFile,String tragargetFile) {
        InputStream io = null;
        OutputStream out = null;
        try {
            io = new FileInputStream(new File(souceFile));
            out = new FileOutputStream(new File(tragargetFile));
            byte[] data = new byte[5*1024];
            int len = 0;
            if((len = io.read(data)) != -1){
                out.write(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (io != null)
                try {
                    io.close();
                } catch (IOException e) {
                    log.error("C2SFileTools.saveFile():输入流关闭失败！", e);
                }
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("C2SFileTools.saveFile():关闭流关闭失败！", e);
                }
        }
    }

    /***
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String parseInputStreamToString(InputStream is) throws IOException {

       StringBuffer stringBuffer = new StringBuffer();
       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"utf-8"));
       String str = null;
        while((str = bufferedReader.readLine()) != null){
            String s = str.toString() + "\n";
           stringBuffer.append(s);
       }
       if(bufferedReader!=null){
           try {
               bufferedReader.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return stringBuffer.toString();
    }

    /***
     * 调用命令行执行搬迁操作
     * @param sourceFile
     * @param targetPath
     * @param targetFileName
     * @return
     * @throws Exception
     */
    public static String moveFile(String sourceFile, String targetPath, String targetFileName) throws Exception {
//        if(StringUtils.isBlank(targetFileName)) {
//            targetFileName = FilePathHelper.getFileNameFromPath(sourceFile);
//        }

        targetPath = targetPath + "/" + targetFileName;
        moveFile(sourceFile, targetPath);
        return targetFileName;
    }

    public static String moveFile2Dir(String sourceFile, String targetPath) throws Exception {
        return moveFile(sourceFile, targetPath, (String)null);
    }

    /***
     * 如何在java程序中调用linux命令或者shell脚本
     转自：http://blog.sina.com.cn/s/blog_6433391301019bpn.html



     在java程序中如何调用linux的命令？如何调用shell脚本呢？

     这里不得不提到java的process类了。

     process这个类是一个抽象类，封装了一个进程（你在调用linux的命令或者shell脚本就是为了执行一个在linux下执行的程序，所以应该使用process类）。

     process类提供了执行从进程输入，执行输出到进程，等待进程完成，检查进程的推出状态，以及shut down掉进程。


     至于详细的process类的介绍放在以后介绍。

     另外还要注意一个类：Runtime类，Runtime类是一个与JVM运行时环境有关的类，这个类是Singleton的。

     这里用到的Runtime.getRuntime()方法是取得当前JVM的运行环境，也是java中唯一可以得到运行环境的方法。（另外，Runtime的大部分方法都是实例方法，也就是说每次运行调用的时候都需要调用到getRuntime方法）

     下面说说Runtime的exec()方法，这里要注意的有一点，就是public Process exec(String [] cmdArray, String [] envp);这个方法中cmdArray是一个执行的命令和参数的字符串数组，数组的第一个元素是要执行的命令往后依次都是命令的参数，envp感觉应该和C中的execve中的环境变量是一样的，envp中使用的是name=value的方式。

     下面说一下，如何使用process来调用shell脚本

     例如，我需要在linux下实行linux命令：sh test.sh,下面就是执行test.sh命令的方法：

     这个var参数就是日期这个201102包的名字。

     String shpath="/test/test.sh";   //程序路径

     Process process =null;

     String command1 = “chmod 777 ” + shpath;
     process = Runtime.getRuntime().exec(command1);
     process.waitFor();




     String var="201102";               //参数

     String command2 = “/bin/sh ” + shpath + ” ” + var;
     Runtime.getRuntime().exec(command2).waitFor();

     * @param sourceFile
     * @param targetFile
     * @throws Exception
     */
    public static void moveFile(String sourceFile, String targetFile) throws Exception {
        int OS = 1;
        //FilePathHelper.mkdirs4File(targetFile);
        sourceFile = StringUtils.overlay(sourceFile, "\"", sourceFile.lastIndexOf("/") + 1, sourceFile.lastIndexOf("/") + 1) + "\"";
        targetFile = StringUtils.overlay(targetFile, "\"", targetFile.lastIndexOf("/") + 1, targetFile.lastIndexOf("/") + 1) + "\"";
        String[] comm = new String[3];
        if(OS == 1) {
            comm[0] = "cmd.exe";
            comm[1] = "/c";
            //comm[2] = "move /Y " + FilePathHelper.formatDir2Win(sourceFile) + " " + FilePathHelper.formatDir2Win(targetFile);
            comm[2] = "move /Y " + sourceFile + " " + targetFile;
            // /y 取消覆盖文件时的提示信息
        } else {
            comm[0] = "/bin/sh";
            comm[1] = "-c";
            comm[2] = "mv -fu " + sourceFile + " " + targetFile;
        }

        exeOSCommond(comm);
    }

    /***
     * java执行命令
     * @param comm
     * @throws Exception
     */
    public static void exeOSCommond(String[] comm) throws Exception {
        Process myproc = null;
        BufferedReader buf = null;

        try {
            myproc = Runtime.getRuntime().exec(comm);

            try {
                myproc.waitFor();
                buf = new BufferedReader(new InputStreamReader(myproc.getErrorStream(), "UTF-8"));
                String str = "";
                StringBuilder sb = new StringBuilder();

                while((str = buf.readLine()) != null) {
                    sb.append(str);
                }

                if(StringUtils.isNotBlank(sb.toString())) {
                    throw new Exception(String.format("execute command [%s] failure! reason : %s", new Object[]{StringUtils.join(comm, ' '), sb.toString()}));
                }
            } catch (InterruptedException var13) {
                throw new Exception(String.format("execute command [%s] Exception!", new Object[]{StringUtils.join(comm, ' ')}), var13);
            }
        } catch (IOException var14) {
            throw new Exception(String.format("execute command [%s] Exception!", new Object[]{StringUtils.join(comm, ' ')}), var14);
        } finally {
            if(null != buf) {
                try {
                    buf.close();
                } catch (IOException var12) {
                    ;
                }
            }

            if(null != myproc) {
                myproc.destroy();
            }

        }

    }
}
