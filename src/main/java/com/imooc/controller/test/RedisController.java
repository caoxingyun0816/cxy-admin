package com.imooc.controller.test;

import com.imooc.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by caoxingyun on 2018/9/3.
 * redis 测试
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/set")
    public String setValue(String key ,String value){
        redisUtil.set(key,value);
        return "success";
    }

    @RequestMapping("/get")
    public String getValue(String key){
        return (String) redisUtil.get(key);
    }

}
