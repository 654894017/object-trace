package com.damon.test.domain.order;


import com.damon.object_trace.Aggregate;

public interface IOrderGateway {

    Aggregate<Order> get(OrderId orderId);

    /**
     * @param orderAggregate
     */
    Long save(Aggregate<Order> orderAggregate);
}
