package com.imooc.service.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by caoxingyun on 2018/8/3.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceImplTest {

    @Autowired
    private MailService mailService;

//    @Test
//    public void sendSimpleMail() throws Exception {
//        mailService.sendSimpleMail("1194441452@qq.com","cxy","hello this is simple mail");//标记为垃圾邮件，发不出去。0.0
//    }

    @Test
    public void testHtmlMail() throws Exception {
        String content="<html>\n" +
                "<body>\n" +
                "    <h3>hello world ! 这是一封Html邮件!</h3>\n" +
                "</body>\n" +
                "</html>";
        mailService.sendHtmlMail("1194441452@qq.com","test simple mail",content);
        System.out.println();
    }

//
//    @Test
//    public void sendAttachmentsMail() {
//        String filePath="e:\\tmp\\application.log";
//        mailService.sendAttachmentsMail("ityouknow@126.com", "主题：带附件的邮件", "有附件，请查收！", filePath);
//    }
//
//    @Test
//    public void sendInlineResourceMail() {
//        String rscId = "neo006";
//        String content="<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\' ></body></html>";
//        String imgPath = "C:\\Users\\summer\\Pictures\\favicon.png";
//
//        mailService.sendInlineResourceMail("ityouknow@126.com", "主题：这是有图片的邮件", content, imgPath, rscId);
//    }
}