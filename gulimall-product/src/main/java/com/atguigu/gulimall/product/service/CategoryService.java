package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author shenjunjie
 * @email rockyshenjunjie@gmail.com
 * @date 2024-03-25 12:53:35
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    // 查处所有分类，并组装成树形解构
    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    //找到categoryId的完整路径
    Long[] findCategoryPath(Long catelogId);

    void updateDetail(CategoryEntity category);
}

