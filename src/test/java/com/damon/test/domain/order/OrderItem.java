package com.damon.test.domain.order;

import com.damon.aggregate.persistence.ID;
import lombok.Data;

@Data
public class OrderItem implements ID<Long> {

    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private Integer amount;
    private Long price;

    public OrderItem(Long id, Long orderId, Long goodsId, String goodsName, Integer amount, Long price) {
        this.id = id;
        this.orderId = orderId;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.amount = amount;
        this.price = price;
    }

    public OrderItem(Long orderId, Long goodsId, String goodsName, Integer amount, Long price) {
        this.orderId = orderId;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.amount = amount;
        this.price = price;
    }

    public OrderItem(Long goodsId, String goodsName, Integer amount, Long price) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.amount = amount;
        this.price = price;
    }

    public OrderItem() {
    }
}
