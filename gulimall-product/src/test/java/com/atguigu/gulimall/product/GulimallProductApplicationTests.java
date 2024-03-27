package com.atguigu.gulimall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OSSClient ossClient;

    @Test
    public void contextLoads() {
        // 测试插入一条数据
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功……");

        CategoryEntity category = categoryService.getById(500);
        String name = category.getName();
        System.out.println(name);
    }

    @Test
    public void ossUpload() throws FileNotFoundException {
        /*
        利用aliyun-sdk-oss，手动实现上传图片
        后期使用配置文件+注入OSSClient，调用方法即可
         */
//        String endpoint = "oss-cn-shanghai.aliyuncs.com";  // 地域节点
//        String accessKeyId = "LTAI5tGNmsHDpCCL81aDse4s";
//        String accessKeySecret = "FETzGymAxTLrKWDRcMupPAKaeBCTFm";
//        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);


//        FileInputStream inputStream = new FileInputStream("/Users/shen/Desktop/mapper.png");
        FileInputStream inputStream = new FileInputStream("/Users/shen/Desktop/spring-overview.png");
        ossClient.putObject("gulimall-rockyshen","spring-overview.png",inputStream);

        ossClient.shutdown();

        System.out.println("上传完成！！");
    }

}
