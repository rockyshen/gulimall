package com.atguigu.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
mybatis-plus的配置类，主要是将插件加入ioc容器
1、分页插件
 */
@Configuration
@MapperScan("com.atguigu.gulimall.product.dao")
@EnableDiscoveryClient
@EnableTransactionManagement   //开启事务！
public class MybatisConfig {

    /* 将mybaits-plus 3.2.0的分页插件，加入ioc容器
    common模块中有：Query + PageUtils
    但是common没有加入ioc容器
    我们只需要new + return + @Bean将分页器插件加到ioc容器中
    Query + PageUtils 就会自动生效。
     */

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        //设置请求的页面大于最大页后的操作，true回到首页；false继续请求
        paginationInterceptor.setOverflow(true);

        // 设置最大单页限制数量，默认500条，-1不受限制
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }
}
