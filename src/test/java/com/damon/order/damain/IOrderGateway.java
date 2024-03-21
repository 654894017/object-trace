package com.damon.order.damain;


import com.damon.object_trace.Aggregate;
import com.damon.order.damain.entity.Order;
import com.damon.order.damain.entity.OrderId;

public interface IOrderGateway {

    Aggregate<Order> get(OrderId orderId);

    /**
     * @param orderAggregate
     */
    void save(Aggregate<Order> orderAggregate);

    void create(Aggregate<Order> orderAggregate);
}
