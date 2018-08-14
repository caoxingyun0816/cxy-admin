package com.imooc.utils;

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


}
