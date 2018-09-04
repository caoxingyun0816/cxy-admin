package com.wondertek.mam.util.mail;

import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {
	public static final int SUCCESS = 1;
	public static final int FAILURE = 0;
	public static int sendMail(String mailFrom, String mailTo, String mailTitle, String mailContent, String mailServer, String mailCount, String mailPassword){
		try{
			//建立邮件会话 
			Properties props = new Properties();
			//存储发送邮件服务器的信息
			props.put("mail.smtp.host", mailServer);
			//同时通过验证 
			props.put("mail.smtp.auth","true"); 
			props.setProperty("mail.smtp.starttls.enable", "true");
			//根据属性新建一个邮件会话 
			Session s=Session.getInstance(props);
			//由邮件会话新建一个消息对象 
			MimeMessage message=new MimeMessage(s); 
			//设置邮件 
			InternetAddress from= new InternetAddress(mailFrom);
			message.setFrom(from); //设置发件人的地址 
			//设置收件人,并设置其接收类型为TO 
			InternetAddress to=new InternetAddress(mailTo);
			message.setRecipient(Message.RecipientType.TO, to); 
			//设置标题 
			message.setSubject(mailTitle);
			//设置信件内容 
			message.setContent(mailContent, "text/html;charset=gbk");//发送HTML邮件
			//message.setText(mailContent); //发送文本邮件 
			//设置发信时间 
			message.setSentDate(new Date()); 
			//存储邮件信息 
			message.saveChanges(); 
			//发送邮件 
			Transport transport=s.getTransport("smtp"); 
			//以smtp方式登录邮箱,第一个参数是发送邮件用的邮件服务器SMTP地址,第二个参数为用户名,第三个参数为密码 
			transport.connect(mailServer, mailCount, mailPassword); 
			//发送邮件,其中第二个参数是所有已设好的收件人地址 
			transport.sendMessage(message,message.getAllRecipients()); 
			transport.close(); 
			
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			return FAILURE;
		}
	}
	
	/**
	 * 验证邮箱格式是否正确
	 * @param emailAddress
	 * @return
	 */
	public static boolean verificationIsEmail(String emailAddress){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(emailAddress);
		return  matcher.matches();
	}
}
