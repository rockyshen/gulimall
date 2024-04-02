package com.atguigu.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCCEPTION(10001,"参数格式校验失败");

    private int code;

    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    BizCodeEnum(int code, String message){
        this.code = code;
        this.message = message;


    }

}
