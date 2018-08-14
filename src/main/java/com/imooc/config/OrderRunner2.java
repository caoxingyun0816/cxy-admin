package com.imooc.config;

import groovy.util.logging.Commons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by caoxingyun on 2018/8/9.
 */
@Component
@Order(2)
public class OrderRunner2 implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(OrderRunner1.class);

    @Override
    public void run(String... strings) throws Exception {
        log.info("order 2 start");
    }
}
