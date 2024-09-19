package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String skuId = (String)params.get("skuId");
        String wareId = (String)params.get("wareId");
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(skuId)){
            wareSkuEntityQueryWrapper.eq("sku_id",skuId);
        }
        if(!StringUtils.isEmpty(wareId)){
            wareSkuEntityQueryWrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wareSkuEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        wareSkuEntityQueryWrapper.eq("ware_id",wareId).eq("sku_id",skuId);
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(wareSkuEntityQueryWrapper);
        if(wareSkuEntities == null || wareSkuEntities.size() == 0){
            // 1.如果没有这个库存记录，就是新增
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            // sku_name、stock_locked；利用openFeign远程调用product模块,根据sku_id进行查询sku_name
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    String skuName = (String)skuInfo.get("skuName");
                    wareSkuEntity.setSkuName(skuName);
                }
            } catch (Exception e) {
                // catch之后，什么也不做！避免因为一个小的feign失败，影响整个insert事务回滚！太不值得了！
//                throw new RuntimeException(e);
            }
            wareSkuEntity.setStockLocked(0);
            baseMapper.insert(wareSkuEntity);
        }
        // 2.如果有库存记录，就更新
        // 根据特定条件进行更新，就不能用基本方法，要用mapper手动写sql了
        baseMapper.addStock(skuId,wareId,skuNum);
    }

}