package com.imooc.exception;

import io.swagger.models.auth.In;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by caoxingyun on 2018/7/18.
 */
@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class ExceptionStatus extends RuntimeException {

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ExceptionStatus(Integer code,String msg){
        super(msg);
        this.code = code;
    }
}
