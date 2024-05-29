package com.damon.order.infra.order.mapper;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.damon.object_trace.Versionable;
import lombok.Data;


@Data
@TableName("demo_order")
public class OrderPO implements Versionable<Long> {
    @Version
    private Integer version;
    private Long id;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
    private String consigneeName;
    private String consigneeShippingAddress;
    private String consigneeMobile;
    private Long totalMoney;
    private Long actualPayMoney;
    private Long couponId;
    private Long deductionPoints;
    private Long orderSubmitUserId;
    private Integer isDelete;
    private Long sellerId;
}
