package com.imooc.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/** freemarker模板引擎
 * Created by caoxingyun on 2018/8/1.
 */
@Component
public class FreemarketUtil {

    public static String xmlGenerateTest() throws TemplateException {
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding("utf-8");
        cfg.setStrictSyntaxMode(true);
        cfg.setNumberFormat("0");
        cfg.setClassForTemplateLoading(FreemarketUtil.class,"/");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("key","cxy");
        List<Map<String,Object>> epgs = new ArrayList<Map<String,Object>>();
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("beginTime","8.1");
        map1.put("endTime","8.1");
        map1.put("programName","西虹市首富");
        Map<String,Object> map2 = new HashMap<String,Object>();
        map2.put("beginTime","8.1");
        map2.put("endTime","8.1");
        map2.put("programName","爱情公寓");
        epgs.add(map1);
        epgs.add(map2);
        map.put("epgs",epgs);
        OutputStreamWriter outputStreamWriter = null;
        try {
            Template template = cfg.getTemplate("/ftl/test.ftl");
            File out = new File("D:/download/test.txt");
            if(!out.getParentFile().exists()){
                out.getParentFile().mkdirs();
            }
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(out),"utf-8");
            template.process(map,outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStreamWriter.toString();
    }

    /***
     * 根据模板生成信息
     * @param templateFile 模板文件
     * @param prams     解析参数
     * @param templateEncode 字符集
     * @return 返回String，根据自己需求可以写入到文件中
     * @throws IOException
     * @throws TemplateException
     */
    public static String generateXml(String templateFile, Map<String, Object> prams, String templateEncode) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding(templateEncode);
        cfg.setEncoding(Locale.getDefault(), templateEncode);
        cfg.setStrictSyntaxMode(true);
        cfg.setNumberFormat("0");
        cfg.setClassForTemplateLoading(FreemarketUtil.class, "/");
        Template template = cfg.getTemplate(templateFile);
        StringWriter out = new StringWriter();
        template.process(prams, out);
        return out.toString();
    }

    /***
     * 写入到指定文件中
     * @param templateFile
     * @param prams
     * @param filePath
     * @param outEncoding
     * @throws IOException
     * @throws TemplateException
     */
    public static void generateXmlToFile(String templateFile, Map<String, Object> prams, String filePath, String outEncoding) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding(outEncoding);
        cfg.setEncoding(Locale.getDefault(), outEncoding);
        cfg.setOutputEncoding(outEncoding);
        cfg.setStrictSyntaxMode(true);
        cfg.setNumberFormat("0");
        cfg.setClassForTemplateLoading(FreemarketUtil.class, "/");
        Template template = cfg.getTemplate(templateFile);
        File localSyncFile = new File(filePath);
        if(!localSyncFile.getParentFile().exists()) {
            localSyncFile.getParentFile().mkdirs();
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, false), outEncoding)), true);
        template.setOutputEncoding(outEncoding);
        template.setEncoding(outEncoding);
        template.process(prams, out);
        out.close();
        out = null;
    }

}
