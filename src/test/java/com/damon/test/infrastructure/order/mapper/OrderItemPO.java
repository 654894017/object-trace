package com.damon.test.infrastructure.order.mapper;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.damon.object_trace.ID;
import lombok.Data;

@Data
@TableName("demo_order_item")
public class OrderItemPO implements ID<Long> {
    @TableId
    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private Integer amount;
    private Long price;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}
