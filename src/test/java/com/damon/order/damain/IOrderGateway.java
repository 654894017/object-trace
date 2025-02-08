package com.damon.order.damain;


import com.damon.object_trace.Aggregate;
import com.damon.order.damain.entity.Order;
import com.damon.order.damain.entity.OrderId;

public interface IOrderGateway {

    Aggregate<Order> get(OrderId orderId);

    /**
     * @param orderAggregate
     */
    Long save(Aggregate<Order> orderAggregate);
}
