package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author rockyshen
 * @date 2024/12/23 12:51
 * 二级分类的VO
 *      静态内部类：三级分类Vo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo {
    private String catalog1Id;             // 它对应的一级分类id

    private List<Catalog3Vo> catalog3List;

    private String id;                    // 它二级分类自己的id
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo{
        private String catalog2Id;    // 它对应的二级分类id
        private String id;           // 它三级分类自己的id
        private String name;
    }
}
