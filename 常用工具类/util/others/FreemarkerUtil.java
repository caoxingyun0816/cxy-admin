package com.wondertek.mam.util.others;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import freemarker.cache.FileTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 简易模板编译
 * @author Kyle Guo
 * 
 * */
public class FreemarkerUtil {
	//模板缓存对象
	private static Map<String, Template> templteMap = new HashMap<String, Template>();
	
	private static Logger log = Logger.getLogger(FreemarkerUtil.class);
	/**
	 * 模板编译
	 * @param map 参数MAP
	 * @param templateDir 模板目录
	 * @param templateName 模板文件
	 * 
	 * @return 编译结果
	 * */
	
	public static String simpleCompile(Map<String, Object> map, String templateDir, String templateName){
		
		String result = "";
		try {
			
			Template tpl = templteMap.get(templateDir+ File.separator + templateName);
			
			if(tpl == null){
				
				FileTemplateLoader tl = new FileTemplateLoader(new File(templateDir));
				freemarker.cache.StringTemplateLoader t2 = new freemarker.cache.StringTemplateLoader();
				Configuration cfg = new Configuration();
				cfg.setEncoding(Locale.getDefault(), "utf-8");
				cfg.setStrictSyntaxMode(true);
				cfg.setWhitespaceStripping(true);
				cfg.setNumberFormat("0");
				
				cfg.setTemplateLoader(tl);
				
				tpl = cfg.getTemplate(templateName);
				templteMap.put(templateDir+ File.separator + templateName , tpl);
			}
			
			StringWriter sw = new StringWriter();
			
			try {
				tpl.process(map, sw);
			} catch (TemplateException e) {
				log.error("errorMsg:"+e.getMessage());
			}
			
			result = sw.toString();

		} catch (IOException e) {
			log.error("errorMsg:"+e.getMessage());
		}
		
		
		return result;
		
	}
	
	/**
	 * 模板编译
	 * @param map 参数MAP
	 * @param templateResource 模板
	 * @return 编译结果
	 * */
	public static String stringCompile(Map<String, Object> map, String templateResource,TemplateModel contId,  TemplateModel imageType1, TemplateModel imageType2){
		String result = "";
		try {
				freemarker.cache.StringTemplateLoader tl = new freemarker.cache.StringTemplateLoader();
				tl.putTemplate("tr", templateResource);
				Configuration cfg = new Configuration();
				cfg.setEncoding(Locale.getDefault(), "utf-8");
				cfg.setStrictSyntaxMode(true);
				cfg.setWhitespaceStripping(true);
				cfg.setNumberFormat("0");				
				cfg.setTemplateLoader(tl);
				Template tpl = cfg.getTemplate("tr");
				StringWriter sw = new StringWriter();
				try {
					Environment env = tpl.createProcessingEnvironment(map, sw,null);
					env.setGlobalVariable("contId", contId);
					env.setGlobalVariable("imageType1", imageType1);
					env.setGlobalVariable("imageType2", imageType2);
					env.process();
				} catch (TemplateException e1) {
					log.error("errorMsg:"+e1.getMessage());
				}			
			result = sw.toString();
		} catch (IOException e) {
			log.error("errorMsg:"+e.getMessage());
		}
		return result;
	}

}
