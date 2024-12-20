package com.atguigu.gulimall.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author rockyshen
 * @date 2024/12/17 15:16
 * es数据库的配置类，将esClient加入ioc容器
 */

@Configuration
public class GulimallElasticsearchConfig {


    @Bean
    public ElasticsearchClient esClient(){
        // Create the low-level client
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        return esClient;
    }
}
