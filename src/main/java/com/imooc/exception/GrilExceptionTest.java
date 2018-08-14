package com.imooc.exception;

import com.imooc.enums.CodeEnum;

/**
 * Created by caoxingyun on 2018/7/17.
 */
// spring 只会对你抛出的异常时runtimeException 就行回滚
public class GrilExceptionTest extends RuntimeException{
    private Integer code;

    GrilExceptionTest(CodeEnum codeEnum){
        super(codeEnum.getMsg());
        this.code = codeEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
