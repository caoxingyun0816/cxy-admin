package com.imooc.quartz;

import com.imooc.utils.FreemarketUtil;
import com.imooc.utils.Httpcomponet;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by caoxingyun on 2018/8/2.
 */
@Component
@Async
//然后在定时任务的类或者方法上添加@Async 。最后重启项目，每一个任务都是在不同的线程中
public class HttpJob {

    private Logger log = LoggerFactory.getLogger(HttpJob.class);

    @Autowired
    private FreemarketUtil freemarketUtil;

//    @Scheduled(cron = "0/5 * * * * ?")   //cron表达式
    public void postHttpJob() throws IOException, TemplateException {
        log.info("now time is ："+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
        String file = "/ftl/test.ftl";
        String xml = freemarketUtil.generateXml(file,map,"utf-8");
        //String url = "http://127.0.0.1:8080/mam-admin/cxyTest";
        String url = "http://127.0.0.1:8888/parse";
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("xml",xml);
        log.info("1"+xml);
        //String response = HttpClientUtil.sendStr(url,xml);
        String response = Httpcomponet.send(url,xml,null);
        //String response = Httpcomponet.send(url,null,param);
        log.info("response is " + response);
    }

//    @Scheduled(fixedRate = 5000)   //5秒执行一次
//    public void timerToZZP(){
//        System.out.println("ZZP:" + new Random().nextLong() + new SimpleDateFormat("HH:mm:ss").format(new Date()));
//    }
//
//    @Scheduled(fixedDelay = 5000)  //5秒后执行
//    public void timerToReportCount(){
//        for (int i = 0; i < 10; i++){
//            System.out.println("<================its" + i + "count===============>" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
//        }
//    }
//
//    @Scheduled(initialDelay = 5000,fixedRate = 6000) // 5秒后，每6秒执行一次
//    public void timerToReport(){
//        for (int i = 0; i < 10; i++){
//            System.out.println("<================delay :" + i + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "count===============>");
//        }
//    }

}
