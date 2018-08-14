package com.imooc.controller.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by caoxingyun
 * 2018-07-18 23:36
 * 登录controller
 */
@Controller
public class LoginController {

    Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping(value = "/")
    public String blank() {
        log.info("blank!");
        return "login/login";
    }

    @GetMapping(value = "/index")
    public String index() {
        log.info("主页!");
        return "index";
    }

    @GetMapping(value = "/login")
    public String login() {
        log.info("登录操作!");
        return "login/login";
    }
    @GetMapping(value = "/main")
    public String main() {
        log.info("frame操作!");
        return "main";
    }
    @GetMapping(value = "/notFound")
    public String notFound() {
        log.info("404!");
        return "404";
    }

}
