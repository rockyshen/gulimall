package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }

    // 查处所有分类，然后组装成树形结构
    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = categoryDao.selectList(null);
        // mysql查询是性能瓶颈，先把所有数据查出来，Java中拿到后，再慢慢处理！
        //2、组装成树形结构
        //2.1 找到所有的一级分类(parent_id = 0)
        List<CategoryEntity> level1Menus = entities.stream()
                // parent_cid=0 表示这个分类是一级分类
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(this.getChildrens(menu,entities));
                    return menu;   // 将子分类映射到父分类，重构一下json，重新返回
                })
                .sorted((menu1, menu2) -> {
                    // sorted的数字越小，排在越前面
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查删除的菜单，有没有被别的地方引用
        baseMapper.deleteBatchIds(asList);  //用逻辑删除！
    }

    @Override
    public Long[] findCategoryPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    @Transactional
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);   // category自己的表先更新掉

        // name字段不为空，证明要更新分类名了
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        }
    }

    // 递归方法
    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;
    }

    /**
     * 用于实现通过调用本方法，查询当前分类的所有子分类
     * 本方法，传入当前的根，和全部数据，返回该根节点的所有子节点
     * @param root  当前菜单
     * @param all   所有菜单
     * 例如，传入一级分类：手机、整个分类menus。就能返回手机这个分类下，所有的子分类：手机通信、运营商、手机配件等等；
     * 查询条件就是parent_cid=当前菜单的cid
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> {
                    return categoryEntity.getParentCid() == root.getCatId();
                })
                .map( categoryEntity -> {
                    // 递归！为什么getChildrens可以不用基于对象调用
                    categoryEntity.setChildren(this.getChildrens(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted((menu1, menu2) -> {
                    // menu1.getSort()==null?0:menu1.getSort() 为了避免对象属性读取到null，形成空指针异常
                    // menu1.getSort()对象属性等于空吗？如果空的话，赋值0，不等于null，就是该对象的属性值！
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());
        // children是一个包含多个category实体的列表
        return children;
    }
}