package com.imooc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by caoxingyun on 2018/8/9.
 * CommandLineRunner 接口的 Component 会在所有 Spring Beans 都初始化之后，
 * SpringApplication.run() 之前执行，非常适合在应用程序启动之初进行一些数据初始化的工作
 * 如果我们在启动容器的时候需要初始化很多资源，并且初始化资源相互之间有序，那如何保证不同的 CommandLineRunner 的执行顺序呢？
 *  Spring Boot 也给出了解决方案。那就是使用 @Order 注解。
 *  添加 @Order 注解的实现类最先执行，并且@Order()里面的值越小启动越早。
 */
@Component
@Order(1)
public class OrderRunner1 implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(OrderRunner1.class);

    @Override
    public void run(String... strings) throws Exception {
        log.info("order 1 start");
    }
}
