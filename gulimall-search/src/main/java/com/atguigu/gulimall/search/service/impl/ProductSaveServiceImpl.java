package com.atguigu.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.atguigu.gulimall.search.constant.EsConstant.PRODUCT_INDEX;

/**
 * @author rockyshen
 * @date 2024/12/18 22:35
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {
        // 利用es的bulk方法，批量向es中存入一组SkuEsModel
        BulkRequest.Builder br = new BulkRequest.Builder();
        skuEsModelList.forEach(skuEsModel ->
            br.operations(op ->
                op.index(idx ->
                    idx.index(PRODUCT_INDEX).id(skuEsModel.getSkuId().toString()).document(skuEsModel)
                )
            )
        );

        BulkResponse bulk = esClient.bulk(br.build());
        // 是否出现批量错误
        boolean b = bulk.errors();
        return b;
    }
}
