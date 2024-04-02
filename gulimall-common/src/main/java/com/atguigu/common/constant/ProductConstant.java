package com.atguigu.common.constant;

/**
 * 商品属性的常量，枚举类
 * attr_base 规格参数，基本属性，attr_type = 1
 * attr_sale 销售属性， attr_type = 0
 */
public enum ProductConstant {
    ATTR_TYPE_BASE(1,"基本属性"),
    ATTR_TYPE_SALE(0,"销售属性");

    private Integer code;

    private String msg;

    ProductConstant(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
