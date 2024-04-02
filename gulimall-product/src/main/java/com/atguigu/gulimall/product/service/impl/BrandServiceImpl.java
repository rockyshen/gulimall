package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.google.j2objc.annotations.AutoreleasePool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /*
        增加品牌管理页面下的模糊查询，param key=“aaa”
         */
        String key = (String)params.get("key");

        //2.重写wrapper
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();

        //1.如果有关键字
        if(!StringUtils.isEmpty(key)){
            // wrapper中加一些条件
            queryWrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 当更新pms_brand时，同步更新关联表，例如：pms_category_brand_relation
     * @param brand
     */
    @Override
    @Transactional  //同时更新两个表，需要开启事务
    public void updateDetail(BrandEntity brand) {
        this.updateById(brand);  // 先更新自己表

        // 如果传递过来的更新信息，brandName也有字符，证明要改名字了！
        // 同步去更新关联表中的数据
        if(!StringUtils.isEmpty(brand.getName())){
            // 1、pms_category_brand_relation这个表需要更新
            // 去CategoryBrandRelationServiceImpl接口实现类里写个方法去保存
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());

            // TODO 更新其他关联

        }

    }

}