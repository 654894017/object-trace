package com.damon.order.infra.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damon.object_trace.Aggregate;
import com.damon.object_trace.exception.EntityNotFoundException;
import com.damon.object_trace.exception.OptimisticLockException;
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
            throw new EntityNotFoundException(String.format("Order (%s) is not found", orderId.getId()));
        }
        List<OrderItemPO> orderItemPOList = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemPO>().eq(OrderItemPO::getOrderId, orderId.getId()));
        return OrderFactory.convert(orderPO, orderItemPOList);
    }

    private Long create(Aggregate<Order> orderAggregate) {
        Order root = orderAggregate.getRoot();
        OrderPO orderPO = OrderFactory.convert(root);
        super.insert(orderPO);
        root.getOrderItems().forEach(orderItem -> {
            orderItem.setOrderId(orderPO.getId());
            OrderItemPO orderItemPO = OrderFactory.convert(orderItem);
            super.insert(orderItemPO);
        });
        return orderPO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(Aggregate<Order> aggregate) {
        if (aggregate.isNew()) {
            return create(aggregate);
        }
        if (!aggregate.isChanged()) {
            return aggregate.getRoot().getId();
        }
        return update(aggregate);
    }

    private Long update(Aggregate<Order> orderAggregate) {
        Order root = orderAggregate.getRoot();
        Order snapshot = orderAggregate.getSnapshot();
        Boolean result = super.executeSafeUpdate(root, snapshot, OrderFactory::convert);
        Boolean result2 = super.executeUpdateList(root.getOrderItems(), snapshot.getOrderItems(), item -> {
            item.setOrderId(root.getId());
            return OrderFactory.convert(item);
        });
        if (!result2 && !result) {
            throw new OptimisticLockException(String.format("Update order (%s) error, it's not found or changed by another user", orderAggregate.getRoot().getId()));
        }
        return root.getId();
    }
}
