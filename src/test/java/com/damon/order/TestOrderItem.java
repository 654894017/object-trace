package com.damon.order;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.damon.order.infra.order.mapper.OrderItemMapper;
import com.damon.order.infra.order.mapper.OrderItemPO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrderApplication.class)
public class TestOrderItem {
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Test
    public void test() {
        LambdaUpdateWrapper<OrderItemPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(OrderItemPO::getGoodsName, "2222223333444");
        wrapper.eq(OrderItemPO::getId, 1770661344966778880L);
        orderItemMapper.update(new OrderItemPO(), wrapper);

    }


}
