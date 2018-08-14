package com.imooc.service;

import com.imooc.domain.Girl;
import com.imooc.service.girl.GirlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by caoxingyun
 * 2018-07-14 23:26
 */
//service 的是测试
@RunWith(SpringRunner.class)//使用底层的Junit测试类
@SpringBootTest//springboot的测试类，开启整个项目
public class GirlServiceTest {

    private Logger logger = LoggerFactory.getLogger(GirlServiceTest.class);
    @Autowired
    private GirlService girlService;

    @Test
    public void findOneTest() throws Exception {
        Girl girl = girlService.findOne(1);
        logger.info("girl:"+girl);
    }

}