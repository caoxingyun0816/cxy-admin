package com.imooc.controller;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/7/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring/applications.xml"}) springmvc 测试
@SpringBootTest
@AutoConfigureMockMvc
//配置事务的回滚,对数据库的增删改都会回滚,便于测试用例的循环利用
//@TransactionConfiguration(transactionManager = "transactionManager",defaultRollback = true)
public class MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void mockMvcGO() throws Exception {
       String string =  mockMvc.perform(MockMvcRequestBuilders.put("/girls/1").contentType(MediaType.APPLICATION_FORM_URLENCODED)
               //.get/.post/.delete/.put直接指定请求的方法类型
                .param("age","20")//传参
                .param("cupSize","B")
               .param("money","500000"))
                .andExpect(MockMvcResultMatchers.status().isOk()) ////返回的状态是200
                //.andDo() //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();//将相应的数据转换为字符串
        System.out.println(string);
    }

    //请求json格式
    @Test
    public void mockMvcNext() throws Exception{
        Map map = new HashMap();
        map.put("1","1");
        map.put("2","2");
        String s = "{'1','2'}";
        mockMvc.perform(MockMvcRequestBuilders.get("/girls").contentType(MediaType.APPLICATION_JSON)//请求为JSOn
        .content(s)).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        //　注意上面contentType需要设置成MediaType.APPLICATION_JSON，即声明是发送“application/json”格式的数据。使用content方法，将转换的json数据放到request的body中
    }
}
