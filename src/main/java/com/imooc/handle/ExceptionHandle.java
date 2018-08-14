package com.imooc.handle;

import com.imooc.domain.Result;
import com.imooc.enums.CodeEnum;
import com.imooc.exception.GirlException;
import com.imooc.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by caoxingyun on 2018/7/17.
 * 异常处理类
 */
@ControllerAdvice
public class ExceptionHandle {
    private Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    //返回前台json数据
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    //@ResponseStatus(value = HttpStatus.NOT_FOUND) 配合ExceptionHandler处理
    //不要轻易把@ResponseStatus修饰目标方法，因为无论它执行方法过程中有没有异常产生，用户都会得到异常的界面，而目标方法正常执行。
    //当然可以配合异常处理类使用
    public Result handle(Exception e) throws Exception {
        if(e instanceof GirlException){
            GirlException GirlException = (GirlException)e;
            return ResultUtil.error(GirlException.getCode(),GirlException.getMessage());
        }
        //如果发现了自定义异常并加了ResponseStatus注解，则执行，达到前台能得到设置的状态码
        if(null != AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class)){
            e.printStackTrace();//打印异常信息
            throw e;
        }else{
            logger.error("异常,你有毒!");
            return ResultUtil.error(CodeEnum.UNKONW_ERROR.getCode(),e.getMessage());
        }
    }
//    /***
//     * 返回异常界面
//     */
//    @ExceptionHandler(value = Exception.class)
//    public ModelAndView handle(HttpServletRequest request,Exception e){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("url",request.getRequestURI());
//        modelAndView.addObject("msg",e.getMessage());
//        modelAndView.setViewName("error");
//        return modelAndView;
//    }
}
