package com.imooc.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by caoxingyun on 2018/8/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FreemarketUtilTest {

    private Logger log = LoggerFactory.getLogger(FreemarketUtil.class);

    @Autowired
    private FreemarketUtil freemarketUtil;

    @Test
    public void xmlGenerate() throws Exception {
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
        String url = "http://127.0.0.1:8080/mam-admin/contentToCipCallBack";
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("xml",xml);
        log.info("1"+xml);
        //String response = HttpClientUtil.sendStr(url,xml);
        //String response = Httpcomponet.send(url,xml,null);
        //String response = Httpcomponet.send(url,null,param);
    }

}