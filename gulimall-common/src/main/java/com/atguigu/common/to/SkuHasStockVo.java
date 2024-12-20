package com.atguigu.common.to;

import lombok.Data;

/**
 * @author rockyshen
 * @date 2024/12/18 16:47
 * 根据skuId,查出这个sku的库存
 */

@Data
public class SkuHasStockVo {
    private Long skuId;

    // 是否有库存，不存具体库存数
    private Boolean hasStock;
}
