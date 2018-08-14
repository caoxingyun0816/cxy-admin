package com.imooc.controller.test;

import com.imooc.exception.ExceptionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by caoxingyun on 2018/7/16.
 * 异常测试
 */
@Controller
public class ExceptionController {
    //@ResponseStatus(value = HttpStatus.BAD_GATEWAY) 不要将responseStatus 用在方法上，因为无论失败还是成功都会返回设置的状态码
    @RequestMapping(value = "/exception/{id}",method = {RequestMethod.GET,RequestMethod.POST})
    public String errorController(@PathVariable Integer id) throws ExceptionStatus {
        if(id%2 != 0){
            throw new ExceptionStatus(1,"失败！");
        }
        return "error";
    }

    @GetMapping("/uploadResult")
    public String uploadResult() throws ExceptionStatus {
        return "uploadResult";
    }
}
