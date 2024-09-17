package com.atguigu.gulimall.ware.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
   @JsonProperty(value = "purchaseId")
   private Long purchaseId; //整单id，用Long声明属性，可以接收null，用基本数据类型，就无法容忍null

   @JsonProperty(value = "items")
   private List<Long> items;//[1,2,3,4] 多个采购需求，purchase_detail_id

   public Long getPurchaseId() {
      return purchaseId;
   }

   public void setPurchaseId(Long purchaseId) {
      this.purchaseId = purchaseId;
   }

   public List<Long> getItems() {
      return items;
   }

   public void setItems(List<Long> items) {
      this.items = items;
   }
}
