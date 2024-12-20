package com.atguigu.common.constant;

/**
 * @author rockyshen
 * @date 2024/12/18 23:24
 * 商品上架状态枚举
 */
public enum ProductStatusConstant {
    NEW_SPU(0,"新建"),
    SPU_UP(1,"商品上架"),
    SPU_DOWN(2,"商品下架");

    private Integer code;

    private String msg;

    ProductStatusConstant(Integer code, String msg) {
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
