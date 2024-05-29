package com.damon.order.damain.entity;

import lombok.Data;

@Data
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private Integer amount;
    private Long price;
//    private Long updateTime;

//    public OrderItem(Long id, Long orderId, Long goodsId, String goodsName, Integer amount, Long price, Long updateTime) {
//        this.id = id;
//        this.orderId = orderId;
//        this.goodsId = goodsId;
//        this.goodsName = goodsName;
//        this.amount = amount;
//        this.price = price;
//        this.updateTime = updateTime;
//    }

    public OrderItem(Long id, Long orderId, Long goodsId, String goodsName, Integer amount, Long price) {
        this.id = id;
        this.orderId = orderId;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.amount = amount;
        this.price = price;
    }

    public OrderItem() {
    }
}
