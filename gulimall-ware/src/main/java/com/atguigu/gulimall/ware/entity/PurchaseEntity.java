package com.atguigu.gulimall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 采购信息
 * 
 * @author shenjunjie
 * @email rockyshenjunjie@gmail.com
 * @date 2024-09-16 14:40:41
 */
@Data
@TableName("wms_purchase")
public class PurchaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	@JsonProperty(value = "id")
	private Long id;
	/**
	 * 
	 */
	@JsonProperty(value = "assigneeId")
	private Long assigneeId;
	/**
	 * 
	 */
	@JsonProperty(value = "assigneeName")
	private String assigneeName;
	/**
	 * 
	 */
	@JsonProperty(value = "phone")
	private String phone;
	/**
	 * 
	 */
	@JsonProperty(value = "priority")
	private Integer priority;
	/**
	 * 
	 */
	@JsonProperty(value = "status")
	private Integer status;
	/**
	 * 
	 */
	@JsonProperty(value = "wareId")
	private Long wareId;
	/**
	 * 
	 */
	@JsonProperty(value = "amount")
	private BigDecimal amount;
	/**
	 * 
	 */
	@JsonProperty(value = "createTime")
	private Date createTime;
	/**
	 * 
	 */
	@JsonProperty(value = "updateTime")
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(Long assigneeId) {
		this.assigneeId = assigneeId;
	}

	public String getAssigneeName() {
		return assigneeName;
	}

	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getWareId() {
		return wareId;
	}

	public void setWareId(Long wareId) {
		this.wareId = wareId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
