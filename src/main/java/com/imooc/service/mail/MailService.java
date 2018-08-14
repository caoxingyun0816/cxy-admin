package com.imooc.service.mail;

/**
 * 邮件接口
 * Created by caoxingyun on 2018/8/3.
 */
public interface MailService {
    public void sendSimpleMail(String to,String subject,String content);

    public void sendHtmlMail(String s, String s1, String content);

    public void sendAttachmentsMail(String s, String s1, String s2, String filePath);

    public void sendInlineResourceMail(String s, String s1, String content, String imgPath, String rscId);
}
