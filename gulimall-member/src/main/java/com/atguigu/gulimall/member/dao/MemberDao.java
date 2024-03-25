package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author shenjunjie
 * @email rockyshenjunjie@gmail.com
 * @date 2024-03-25 23:35:00
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
