package com.imooc.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


/**
 * Created by caoxingyun on 2018/7/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc//模拟测试controller ，自动装配的注解
public class GirlControllerTestDeno {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void girlList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/girls"))//执行，绑定请求路径
                .andExpect(MockMvcResultMatchers.status().isOk())//判断请求的返回码，isOk 是200。
                .andExpect(MockMvcResultMatchers.content().string("123"));//andExpect 做判断，是否相等
        //MockMvcResultMatchers.content() 返回的内容
    }

}