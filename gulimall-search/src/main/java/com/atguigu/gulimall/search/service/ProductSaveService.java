package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author rockyshen
 * @date 2024/12/18 22:34
 */
public interface ProductSaveService {


    Boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
