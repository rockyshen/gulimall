package com.atguigu.common.exception;

/**
 * 错误码列表
 * 10:通用
 *      001:参数格式校验
 * 11:商品
 * 12:订单
 * 13:购物车
 * 14:物流
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

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
