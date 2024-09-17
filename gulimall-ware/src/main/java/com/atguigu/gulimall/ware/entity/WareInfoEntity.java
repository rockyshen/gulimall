package com.atguigu.gulimall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 仓库信息
 * 
 * @author shenjunjie
 * @email rockyshenjunjie@gmail.com
 * @date 2024-09-16 14:40:41
 */
@Data
@TableName("wms_ware_info")
public class WareInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@JsonProperty(value="id")
	private Long id;
	/**
	 * 仓库名
	 */
	@JsonProperty(value="name")     //配置请求体JSON中的key，和实体类的对应！
	private String name;
	/**
	 * 仓库地址
	 */
	@JsonProperty(value="address")
	private String address;
	/**
	 * 区域编码
	 */
	@JsonProperty(value="areacode")
	private String areacode;

}
