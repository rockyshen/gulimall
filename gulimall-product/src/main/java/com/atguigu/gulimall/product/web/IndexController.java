package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author rockyshen
 * @date 2024/12/22 22:38
 * web包，主要使用thymeleaf，进行视图解析器！
 */
@Controller    // @RestController是返回JSON，这里用视图解析器，所以要用@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        // 查一级菜单
        List<CategoryEntity> categoryEntityList =  categoryService.getLevel1Categorys();

        // 视图解析器，拼接：类路径/templates + 返回值 + html
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    // 对接catalogLoader.js，基于一级分类，获取所有子分类，以JSON返回
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String,List<Catalog2Vo>> getCategoryJson(){
        // 不能写成对象，因为key是每个一级分类id，需要用map自定义！
        Map<String,List<Catalog2Vo>> map = categoryService.getCategoryJson();
        return map;
    }
}
