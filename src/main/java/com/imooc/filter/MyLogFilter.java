package com.imooc.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Created by caoxingyun on 2018/7/30.
 * 日志过滤器
 */
public class MyLogFilter extends Filter<ILoggingEvent>{
    @Override
    public FilterReply decide(ILoggingEvent event){
        if(event.getMessage().contains("cxy")){
            return FilterReply.ACCEPT;//允许输入串
        }else{
            return FilterReply.DENY;//不允许输出
        }
    }
}
