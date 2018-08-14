package com.imooc.controller.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by caoxingyun on 2018/7/23.
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    @GetMapping("/message")
    public String messageView(){
        return "/message/message";
    }


    @GetMapping("/messageReply")
    public String messageReplayView(){
        return "/message/messageReply";
    }
}
