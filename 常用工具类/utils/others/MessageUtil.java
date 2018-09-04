package com.wondertek.mam.util.others;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class MessageUtil {
	private final static Log log = LogFactory.getLog(MessageUtil.class);
	
	private Configuration cfg = new Configuration();
	
	/**
	 * 发送文本内容邮件
	 * @param mailSender 
	 * @param mailTo 收件人
	 * @param mailFrom 发送人
	 * @param subject 邮件主题
	 * @param tplParamMap 模板变量  
	 * @throws Exception
	 */
	public boolean sendTextMail(JavaMailSender mailSender,String mailTo,String mailFrom,String subject,Map<String,String> tplParamMap,String tplType) {
		try {
			SimpleMailMessage mailMsg = new SimpleMailMessage();  
			mailMsg.setFrom(mailFrom); //发送人  
			mailMsg.setTo(mailTo); //接收人  
			mailMsg.setSubject(subject);

			//嵌入ftl模版  
			cfg.setClassForTemplateLoading(getClass(), "/");
			cfg.setEncoding(Locale.CHINESE, "utf-8");
			Template tpl = cfg.getTemplate(tplType);
			tpl.setEncoding("GBK");
			StringWriter writer = new StringWriter();
			tpl.process(tplParamMap, writer);
			//把模版内容写入邮件中  
			mailMsg.setText(writer.toString());
			mailMsg.setSentDate(new Date());
			mailSender.send(mailMsg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 发送文本内容邮件
	 * @param mailSender 
	 * @param mailTo 收件人
	 * @param mailFrom 发送人
	 * @param subject 邮件主题
	 * @param tplParamMap 模板变量  
	 * @throws Exception
	 */
	public boolean sendTextMail(JavaMailSender mailSender,String[] mailTo,String mailFrom,String subject,Map<String,String> tplParamMap,String tplType) {
		try {
			SimpleMailMessage mailMsg = new SimpleMailMessage();  
			mailMsg.setFrom(mailFrom); //发送人  
			mailMsg.setTo(mailTo); //接收人  
			mailMsg.setSubject(subject);

			//嵌入ftl模版  
			cfg.setClassForTemplateLoading(getClass(), "/");
			cfg.setEncoding(Locale.CHINESE, "utf-8");
			Template tpl = cfg.getTemplate(tplType);
			tpl.setEncoding("GBK");
			StringWriter writer = new StringWriter();
			tpl.process(tplParamMap, writer);
			//把模版内容写入邮件中  
			mailMsg.setText(writer.toString());
			mailMsg.setSentDate(new Date());

			mailSender.send(mailMsg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 发送HTML内容邮件
	 * @param mailSender
	 * @param freeMarker
	 * @param mailTo
	 * @param mailFrom
	 * @param subject
	 * @param tplParamMap
	 * @throws Exception
	 */
	public void sendHtmlMail(JavaMailSender mailSender,FreeMarkerConfigurer freeMarker,String mailTo,String mailFrom,String subject,Map<String,String> tplParamMap) throws Exception {
		MimeMessage msg = mailSender.createMimeMessage();
		// 设置utf-8或GBK编码，否则邮件会有乱码，true表示为multipart邮件
		MimeMessageHelper helper = new MimeMessageHelper(msg, true, "utf-8");
		helper.setTo(mailTo); // 邮件接收地址
		helper.setFrom(mailFrom); // 邮件发送地址,这里必须和xml里的邮件地址相同一致
		helper.setSubject(subject); // 主题
		String htmlText = this.getMailText(freeMarker,tplParamMap); // 使用模板生成html邮件内容
		helper.setText(htmlText, true); // 邮件内容，注意加参数true，表示启用html格式
		mailSender.send(msg); // 发送邮件
		log.debug("mailTo:" + mailTo + ",mailFrom:" + mailFrom + ",subject:" + subject + ",htmlText:" + htmlText + ",msg:" + msg + ",mailSender:" + mailSender);
	}
	private String getMailText(FreeMarkerConfigurer freeMarker,Map<String,String> tplParamMap) throws Exception {
		String htmlText = "";
		// 通过指定模板名获取FreeMarker模板实例
		Template tpl = freeMarker.getConfiguration().getTemplate("mail-html.ftl");
		// 解析模板并替换动态数据，最终content将替换模板文件中的${content}标签。
		htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, tplParamMap);
		return htmlText;
	}
	public static void main(String[] args) throws Exception {  
		for (int j = 0; j < 4; j++) {
			ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext-mail.xml");  
			JavaMailSender sender = (JavaMailSender) ctx.getBean("mailSender");  
			MessageUtil springMail = new MessageUtil();  
			String mailTo = "13761624929@139.com";
			String mailFrom = "13761624929@139.com";
			String subject = "test java mail simple ";
			//        String subject = "test java mail simple ";
			Map<String,String> tplParamMap = new HashMap<String,String>();
			tplParamMap.put("userName", "shliuzw");
			tplParamMap.put("taskName", "test task");
			tplParamMap.put("limitTime","from no");
			tplParamMap.put("formName","from no");
			tplParamMap.put("formNo","from no");

			boolean flag = springMail.sendTextMail(sender, mailTo, mailFrom, subject, tplParamMap,"mail-text.ftl");  
			//        if(!flag)
			//        	continue;
			Thread.sleep(1000);
		}
		//		String orgids = ",1,2,3,4,";
		//		System.out.println(orgids.replaceAll(",2,", ","));
	}
}
