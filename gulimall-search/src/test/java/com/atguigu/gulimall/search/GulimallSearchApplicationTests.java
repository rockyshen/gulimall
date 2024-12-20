package com.atguigu.gulimall.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.discovery.SelectorResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;


@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private ElasticsearchClient esClient;

    // 测试向es中存入一个索引
    @Test
    void indexData() throws IOException {
        User user = new User("zhangsan","Male",33);

//        CreateIndexResponse response = esClient.indices().create(i ->
//                i.index("users"));

        // 直接传递对象
        IndexResponse response = esClient.index(i -> i
                .id("2")
                .index("users")
                .document(user)
        );

        System.out.println(response);
    }

    @Data
    @AllArgsConstructor
    class User{
        private String name;
        private String gender;
        private Integer age;
    }

    // 测试检索数据
    @Test
    void searchData() throws IOException {
//        TermQuery query = QueryBuilders.term().field("address").value("mill").build();
//        SearchRequest request = new SearchRequest.Builder().index("bank").query(query._toQuery()).build();
//        SearchResponse<Account> search = esClient.search(request,Account.class);


        TermQuery termQuery = TermQuery.of(t -> t.field("address").value("mill"));
        SearchResponse<Account> search = esClient.search(s -> s
                .index("bank")
                .query(termQuery._toQuery()),
                Account.class
        );

        for(Hit<Account> hit : search.hits().hits()){
            Account account = hit.source();
            System.out.println(account);
        }

    }

    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }


}
