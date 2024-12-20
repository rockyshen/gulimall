package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductStatusConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDescDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.atguigu.common.constant.ProductStatusConstant.SPU_UP;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 从后台管理系统获取一个大JSON，保存商品的完整信息
     * 2024.4.8 完整跑通了
     * TODO 如果保存失败的情况，在高级篇里补充！
     * @param vo
     */
    @Transactional   //商品完整JSON的大保存，必须加上事务
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.保存Spu基本信息：spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2.保存描述图片，spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3.保存图片集，spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveSpuImages(infoEntity.getId(),images);

        //4.保存spu规格参数：product_attr_values
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setSpuId(infoEntity.getId());
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        //6.2 保存spu的积分信息:gulimall_sms -> sms_spu_bounds
        // openfeign调用 gulimall-coupon
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);

        //判断远程调用是否返回success
        Integer code = (Integer)r.get("code");
        System.out.println("********"+code.toString());
        if (code != 0){
            log.error("远程保存spu积分信息失败！");
        }

        //5.保存spu对应所有sku信息
            // 5.1 sku的基本信息 sku_info
        List<Skus> skus = vo.getSkus();
        skus.forEach(item -> {
            String defaultImg = "";
            for (Images image : item.getImages()) {
                if(image.getDefaultImg()==1){
                    defaultImg = image.getImgUrl();
                }
            }
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(item,skuInfoEntity);
            skuInfoEntity.setBrandId(infoEntity.getBrandId());
            skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setSpuId(infoEntity.getId());
            skuInfoEntity.setSkuDefaultImg(defaultImg);
            skuInfoService.saveSkuInfo(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();

            List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();

                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setImgUrl(img.getImgUrl());
                skuImagesEntity.setDefaultImg(img.getDefaultImg());
                return skuImagesEntity;
            }).filter(entity -> {
                return !StringUtils.isEmpty(entity.getImgUrl()); // 没有图片，路径无需保存,imgUrl有的才会通过
            }).collect(Collectors.toList());

            //5.2 sku的图片信息 sku_images
            skuImagesService.saveBatch(imagesEntities);

            //5.3 sku的销售属性 sku_sale_attr_value
            List<Attr> attr = item.getAttr();
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

            //6.1 sku的优惠、满减信息（跨库 gulimall-sms -> sms_sku_ladder、sms_sku_full_reduction、sms_member_price
            // openfign 调用远程服务 gulimall-coupon
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(item,skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            // 满减信息中如果有0的，过滤掉
            if(skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                // 判断远程调用是否返回success
                Integer code1 = (Integer)r1.get("code");
                System.out.println("********"+code1.toString());
                if (code1 != 0){
                    log.error("远程保存sku积分信息失败！");
                }
            }

        });




    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    // spu上架，查出该spuId对应的所有sku，并存es
    @Override
    public void up(Long spuId) {
        // 查出spuId对应所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);

        // 查出Attrs attrs可以根据spuId查出所有，没必要在sku循环里查(product_attr_value)
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.productAttrListforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        // 传入一组id,返回search_type=1的id数组，表示可以被搜索的attr
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attr> attrList = baseAttrs.stream().filter(item -> {
            // 所有attrId中，挑选出在set中的（也就是可以被搜索的属性）
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());

        // 远程调用ware模块查，不要在循环内查，多次远程调用，性能差
        Map<Long, Boolean> stockMap = new LinkedHashMap<>();
        try {
            List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            List<SkuHasStockVo> skuHasStockVoList =  (List<SkuHasStockVo>)skuHasStock.get("data");

            skuHasStockVoList.stream().forEach(skuHasStockVo -> {
                System.out.println(skuHasStockVo.getSkuId());
                System.out.println(skuHasStockVo.getHasStock());
            });

            // 期望变成map  skuId:hasStock    1:true   2:false
            stockMap = skuHasStockVoList.stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        } catch (Exception e) {
            log.info("库存服务查询异常，原因 =>",e);
        }

        // 封装es model
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> esModelList = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            // 能对拷的对拷
            BeanUtils.copyProperties(sku, esModel);
            // 不能对拷的，要手动查
            esModel.setSkuImg(sku.getSkuDefaultImg());
            esModel.setHotScore(0L);

            // hasStock
            if(finalStockMap == null){
                esModel.setHasStock(true);
            }
            esModel.setHasStock(finalStockMap.get(sku.getSkuId()));

            // brandName、brandImg要根据ID查
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());
            // 来自第217行：attrList属性
            esModel.setAttrs(attrList);
            return esModel;
        }).collect(Collectors.toList());

        // 传递给gulimall-search
        R r = searchFeignService.productStatusUp(esModelList);
        if(r.getCode() == 0){
            // 远程调用成功
            // 修改'pms_spu_info'中的‘publish_status’为1
            baseMapper.updateSpuStatus(spuId, SPU_UP.getCode());
        }else{
            // 远程调用失败，失败重试！
        }
    }

}