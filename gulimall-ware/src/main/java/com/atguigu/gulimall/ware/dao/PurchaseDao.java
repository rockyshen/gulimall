package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 * 
 * @author shenjunjie
 * @email rockyshenjunjie@gmail.com
 * @date 2024-09-16 14:40:41
 */
@Mapper  // TODO 主启动加了@MapperScan，可以不用加@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {
	
}
