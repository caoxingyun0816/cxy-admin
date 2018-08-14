package com.imooc.controller.links;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by caoxingyun on 2018/7/23.
 */
@Controller
@RequestMapping("/links")
public class LinksController {

    @GetMapping("/linksAdd")
    public String linksAdd(){
        return "/links/linksAdd";
    }

    @GetMapping("/linksList")
    public String linksList(){
        return "/links/linksList";
    }


}
