package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author shenjunjie
 * @email rockyshenjunjie@gmail.com
 * @date 2024-03-25 22:51:45
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
