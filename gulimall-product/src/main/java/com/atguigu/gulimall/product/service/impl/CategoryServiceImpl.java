package com.atguigu.gulimall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = categoryDao.selectList(null);
        //2、组装成树形结构
        //2.1 找到所有的一级分类(parent_id = 0)
        List<CategoryEntity> level1Menus = entities.stream()
                .filter((categoryEntity) -> {
                    return categoryEntity.getParentCid() == 0;
                })
                .map((menu) -> {
                    menu.setChildren(getChildrens(menu,entities)); return menu;   // 将子分类映射到父分类，重构一下json，重新返回
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })  // sorted的数字越小，排在越前面
                .collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查删除的菜单，有没有被别的地方引用

        baseMapper.deleteBatchIds(asList);  //用逻辑删除！

    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){  // root是当前对象，all是所有对象

        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> {
                    return categoryEntity.getParentCid() == root.getCatId();
                })
                .map((categoryEntity) -> {
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));    // TODO 递归！为什么getChildrens可以不用基于对象调用
                    return categoryEntity;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());

        return children;   // children是一个包含多个category实体的列表
    }

}