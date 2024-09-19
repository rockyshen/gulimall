package com.atguigu.gulimall.ware.vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {
    //{itemId:1,status:4,reason:""}
    private Long itemId;      // 就是purchaseDetailId
    private Integer status;   // purchaseDetail的状态：新建、正在采购、采购失败
    private String reason;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
