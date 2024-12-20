package com.atguigu.gulimall.search.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static com.atguigu.common.exception.BizCodeEnum.PRODUCT_UP_EXCEPTION;

/**
 * @author rockyshen
 * @date 2024/12/18 22:29
 * 向es中存数据
 */

@RestController
@RequestMapping("search/save")
@Slf4j
public class ElasticSaveController {
    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList){

        try {
            Boolean b = productSaveService.productStatusUp(skuEsModelList);
            return R.ok();
        } catch (Exception e) {
//            log.error("ElasticSaveController商品上架错误：",e);
            return R.error(PRODUCT_UP_EXCEPTION.getCode(),PRODUCT_UP_EXCEPTION.getMessage());
        }


    }
}
