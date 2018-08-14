package com.imooc.utils;

import com.imooc.entity.AssetList;
import com.imooc.entity.XmlParse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.digester3.Digester;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/8/2.
 */
@Component
public class XmlUtil {

    /**将XML解析为对应的bean
     *
     * @param xmlStr
     * @return
     */
    public static Object parseAsset(String xmlStr){
        final Digester digester = new Digester();
        digester.setClassLoader(AssetList.class.getClassLoader());
        digester.setValidating(false);
        digester.addObjectCreate("SyncContentsResult",XmlParse.class);
        digester.addSetProperties("SyncContentsResult","serialNo","serialNo");
        digester.addSetProperties("SyncContentsResult","TimeStamp","TimeStamp");
        digester.addObjectCreate("SyncContentsResult/Assets/Asset",AssetList.class);
        digester.addSetProperties("SyncContentsResult/Assets/Asset", "ID", "id");
        digester.addSetProperties("SyncContentsResult/Assets/Asset", "currentID", "currentId");
        digester.addSetProperties("SyncContentsResult/Assets/Asset", "type", "type");
        digester.addSetProperties("SyncContentsResult/Assets/Asset", "op", "operation");
        digester.addSetProperties("SyncContentsResult/Assets/Asset", "result", "syncStatus");
        digester.addSetProperties("SyncContentsResult/Assets/Asset", "desc", "syncDesc");
        digester.addSetNext("SyncContentsResult/Assets/Asset","addAsset");
        XmlParse result = null;
        try {
              result = (XmlParse) digester.parse(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }




//    public static String generateXml(String templateFile, Map<String, Object> prams, String templateEncode) throws IOException, TemplateException {
//        Configuration cfg = new Configuration();
//        cfg.setDefaultEncoding(templateEncode);
//        cfg.setEncoding(Locale.getDefault(), templateEncode);
//        cfg.setStrictSyntaxMode(true);
//        cfg.setNumberFormat("0");
//        cfg.setClassForTemplateLoading(XmlUtil.class, "/");
//        Template template = cfg.getTemplate(templateFile);
//        StringWriter out = new StringWriter();
//        template.process(prams, out);
//        return out.toString();
//    }
//
//    public static void generateXmlToFile(String templateFile, Map<String, Object> prams, String filePath) throws IOException, TemplateException {
//        generateXmlToFile(templateFile, prams, filePath, "UTF-8");
//    }
//
//    public static void generateXmlToFile(String templateFile, Map<String, Object> prams, String filePath, String outEncoding) throws IOException, TemplateException {
//        Configuration cfg = new Configuration();
//        cfg.setDefaultEncoding(outEncoding);
//        cfg.setEncoding(Locale.getDefault(), outEncoding);
//        cfg.setOutputEncoding(outEncoding);
//        cfg.setStrictSyntaxMode(true);
//        cfg.setNumberFormat("0");
//        cfg.setClassForTemplateLoading(XmlUtil.class, "/");
//        Template template = cfg.getTemplate(templateFile);
//        File localSyncFile = new File(filePath);
//        if(!localSyncFile.getParentFile().exists()) {
//            localSyncFile.getParentFile().mkdirs();
//        }
//
//        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, false), outEncoding)), true);
//        template.setOutputEncoding(outEncoding);
//        template.setEncoding(outEncoding);
//        template.process(prams, out);
//        out.close();
//        out = null;
//    }
//
//    /***
//     *  根据XML解析为对应的对象
//     * @param xmlTag
//     * @param xmlTagMap
//     * @param beanClass
//     * @param xmlStream
//     * @return
//     * @throws IOException
//     * @throws SAXException
//     */
//    public static Object generateXmlStream2Bean(String xmlTag, Map<String, String> xmlTagMap, Class<?> beanClass, InputStream xmlStream) throws IOException, SAXException {
//        Digester digester = new Digester();
//        digester.setValidating(false);
//        digester.addObjectCreate(xmlTag, beanClass);
//        Iterator it = xmlTagMap.keySet().iterator();
//
//        while(it.hasNext()) {
//            String tag = (String)it.next();
//            String value = (String)xmlTagMap.get(tag);
//            digester.addCallMethod(tag, value, 0);
//        }
//
//        return digester.parse(xmlStream);
//    }
}