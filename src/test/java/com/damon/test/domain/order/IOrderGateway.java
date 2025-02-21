package com.damon.test.domain.order;


import com.damon.aggregate.persistence.Aggregate;

public interface IOrderGateway {

    Aggregate<Order> get(OrderId orderId);

    /**
     * @param orderAggregate
     */
    Long save(Aggregate<Order> orderAggregate);
}
