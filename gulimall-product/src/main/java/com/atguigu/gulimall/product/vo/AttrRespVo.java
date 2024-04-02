package com.atguigu.gulimall.product.vo;

import lombok.Data;
import org.w3c.dom.Attr;

@Data
public class AttrRespVo extends AttrVo {
    /**
     * 页面响应多两个信息
     * catelogName：所属分类名字
     * groupName：所属分组
     */
    private String catelogName;

    private String groupName;

    private Long[] catelogPath;
}
