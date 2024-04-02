package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 商品三级分类
 * 
 * @author shenjunjie
 * @email rockyshenjunjie@gmail.com
 * @date 2024-03-25 12:53:35
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId
	private Long catId;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父分类id
	 */
	private Long parentCid;
	/**
	 * 层级
	 */
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 * 逻辑删除注解，1表示未删除，0表示已删除
	 */
	@TableLogic(value = "1", delval = "0")
	private Integer showStatus;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;

	// 声明这个不用映射mysql的表单，是我们代码需要用的
	@JsonInclude(JsonInclude.Include.NON_EMPTY)  //这个字段不为空的时候才包含到json中，级联菜单有用！
	@TableField(exist = false)
	private List<CategoryEntity> children;


	/*
	lombok的@Data注解失效，手动加了getter、setter方法后就能获取到数据！

	把lombok更新到最新版，试一下！
	 */
//	public Long getCatId() {
//		return catId;
//	}
//
//	public void setCatId(Long catId) {
//		this.catId = catId;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public Long getParentCid() {
//		return parentCid;
//	}
//
//	public void setParentCid(Long parentCid) {
//		this.parentCid = parentCid;
//	}
//
//	public Integer getCatLevel() {
//		return catLevel;
//	}
//
//	public void setCatLevel(Integer catLevel) {
//		this.catLevel = catLevel;
//	}
//
//	public Integer getShowStatus() {
//		return showStatus;
//	}
//
//	public void setShowStatus(Integer showStatus) {
//		this.showStatus = showStatus;
//	}
//
//	public Integer getSort() {
//		return sort;
//	}
//
//	public void setSort(Integer sort) {
//		this.sort = sort;
//	}
//
//	public String getIcon() {
//		return icon;
//	}
//
//	public void setIcon(String icon) {
//		this.icon = icon;
//	}
//
//	public String getProductUnit() {
//		return productUnit;
//	}
//
//	public void setProductUnit(String productUnit) {
//		this.productUnit = productUnit;
//	}
//
//	public Integer getProductCount() {
//		return productCount;
//	}
//
//	public void setProductCount(Integer productCount) {
//		this.productCount = productCount;
//	}
//
//	public List<CategoryEntity> getChildren() {
//		return children;
//	}
//
//	public void setChildren(List<CategoryEntity> children) {
//		this.children = children;
//	}
}
