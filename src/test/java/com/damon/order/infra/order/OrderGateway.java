package com.damon.order.infra.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damon.object_trace.Aggregate;
import com.damon.object_trace.mybatis.MybatisRepositorySupport;
import com.damon.order.damain.IOrderGateway;
import com.damon.order.damain.entity.Order;
import com.damon.order.damain.entity.OrderId;
import com.damon.order.infra.order.mapper.OrderItemMapper;
import com.damon.order.infra.order.mapper.OrderItemPO;
import com.damon.order.infra.order.mapper.OrderMapper;
import com.damon.order.infra.order.mapper.OrderPO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class OrderGateway extends MybatisRepositorySupport implements IOrderGateway {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderGateway(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public Aggregate<Order> get(OrderId orderId) {
        OrderPO orderPO = orderMapper.selectById(orderId.getId());
        if (orderPO == null) {
            return null;
        }
        List<OrderItemPO> orderItemPOList = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemPO>().eq(OrderItemPO::getOrderId, orderId.getId()));
        return OrderFactory.convert(orderPO, orderItemPOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Aggregate<Order> orderAggregate) {
        Order root = orderAggregate.getRoot();
        OrderPO orderPO = OrderFactory.convert(root);
        orderMapper.insert(orderPO);
        root.getOrderItems().forEach(orderItem -> {
            OrderItemPO orderItemPO = OrderFactory.convertPO(orderItem);
            orderItemMapper.insert(orderItemPO);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Aggregate<Order> orderAggregate) {
        if (!orderAggregate.isChanged()) {
            return;
        }
        Order root = orderAggregate.getRoot();
        Order snapshot = orderAggregate.getSnapshot();

        Boolean result = super.executeSafeUpdate(root, snapshot, OrderFactory::convert);
        if (!result) {
            throw new RuntimeException(String.format("Update order (%s) error, it's not found or changed by another user", orderAggregate.getRoot().getId()));
        }

        Boolean result2 = super.executeUpdateList(root.getOrderItems(), snapshot.getOrderItems(), OrderFactory::convertPO);
        if (!result2) {
            throw new RuntimeException(String.format("Update order (%s) error, it's not found or changed by another user", orderAggregate.getRoot().getId()));
        }
    }
}
