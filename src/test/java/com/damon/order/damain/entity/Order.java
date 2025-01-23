package com.damon.order.damain.entity;


import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Order{

    private Long id;
    private Integer status;
    private Consignee consignee;
    private List<OrderItem> orderItems;
    private Long totalMoney;
    private Long actualPayMoney;
    private Integer version;
    private Long couponId;
    private Long deductionPoints;
    private Long orderSubmitUserId;
    private Long deleted;
    private Long sellerId;
}
