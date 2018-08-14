package com.imooc.controller.AsyRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by caoxingyun on 2018/8/3.
 */
@RestController
@RequestMapping("/asy")
public class AsyRequest {
    private static Logger log = LoggerFactory.getLogger(AsyRequest.class);

    @PostMapping("/parse")
    public String paseData(HttpEntity<String> requestEntity, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("post xml 请求");
        String xml = requestEntity.getBody();
        log.info("xml is "+ xml);
        //String xml = FileUtil.parseInputStreamToString(request.getInputStream());
        log.info(xml);
        String json = "ok|succ";
        return json;
    }
}
