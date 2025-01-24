package com.damon.order;

import cn.hutool.core.util.IdUtil;
import com.damon.object_trace.Aggregate;
import com.damon.order.damain.IOrderGateway;
import com.damon.order.damain.entity.Order;
import com.damon.order.damain.entity.OrderId;
import com.damon.order.damain.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrderApplication.class)
public class TestOrderGateway {
    @Autowired
    private IOrderGateway orderGateway;

    @Test
    public void test() {
        Aggregate<Order> orderAggregate = orderGateway.get(new OrderId(2L));
        Order order = orderAggregate.getRoot();
        order.setStatus(order.getStatus() + 1);
        List<OrderItem> item = order.getOrderItems();
        item.add(new OrderItem(IdUtil.getSnowflakeNextId(), 2l, 1l, "1", 1, 1l));
        item.get(1).setGoodsId(IdUtil.getSnowflakeNextId());
        item.remove(0);
        orderGateway.save(orderAggregate);
    }

}
