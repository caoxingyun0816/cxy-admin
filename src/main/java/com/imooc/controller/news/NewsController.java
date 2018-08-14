package com.imooc.controller.news;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by caoxingyun on 2018/7/23.
 */
@Controller
@RequestMapping("/news")
public class NewsController {
    @GetMapping("/newsList")
    public String newsListView(){
        return "/news/newsList";
    }

    @GetMapping("/newsAdd")
    public String newsAddView(){
        return "/news/newsAdd";
    }
}
