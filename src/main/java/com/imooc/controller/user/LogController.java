package com.imooc.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by caoxingyun on 2018/7/26.
 */
@RestController
public class LogController {
    //Logger是slf4j包中的logger,slf4j是一个工厂，可以和log4j、logback结合使用
    //引用了logback那么Logger就会使用logback相关的方法来处理日志，引用了log4j也同理
    //这样能便于我们切换日志的实际处理类，这是工厂模式的优点。
    private Logger log = LoggerFactory.getLogger(LoggerFactory.class);


    @GetMapping("/logger")
    public String logTest(){
        //启动springboot 发现debug不打印，springboot 默认使用logback,日志级别为info
        log.info("info who are you ?");
        log.debug("debug i love you ");
        log.error("i m fail ");
        log.warn("warn");
        return "OK!";
    }
}
