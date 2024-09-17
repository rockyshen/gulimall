package com.atguigu.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.atguigu.gulimall.ware.dao")  //为什么要包扫描？TODO 思考：什么情况下需要MapperScan，什么情况下不需要
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
