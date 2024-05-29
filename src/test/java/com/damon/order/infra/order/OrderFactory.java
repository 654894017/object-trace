package com.damon.order.infra.order;


import com.damon.object_trace.Aggregate;
import com.damon.object_trace.AggregateFactory;
import com.damon.order.damain.entity.Consignee;
import com.damon.order.damain.entity.Order;
import com.damon.order.damain.entity.OrderItem;
import com.damon.order.infra.order.mapper.OrderItemPO;
import com.damon.order.infra.order.mapper.OrderPO;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class OrderFactory {

    public static OrderPO convert(@NonNull Order order) {
        OrderPO orderPO = new OrderPO();
        orderPO.setId(order.getId());
        orderPO.setConsigneeMobile(order.getConsignee().getMobile());
        orderPO.setConsigneeName(order.getConsignee().getName());
        orderPO.setConsigneeShippingAddress(order.getConsignee().getShippingAddress());
//        orderPO.setCreateTime(order.getCreateTime());
//        orderPO.setUpdateTime(order.getUpdateTime());
        orderPO.setDeductionPoints(order.getDeductionPoints());
        orderPO.setSellerId(order.getSellerId());
        orderPO.setTotalMoney(order.getTotalMoney());
        orderPO.setOrderSubmitUserId(order.getOrderSubmitUserId());
        orderPO.setStatus(order.getStatus());
        orderPO.setVersion(order.getVersion());
        orderPO.setCouponId(order.getCouponId());
        return orderPO;
    }

    public static List<OrderItemPO> convertPO(@NonNull List<OrderItem> itemList) {
        return itemList.stream().map(OrderFactory::convertPO).collect(Collectors.toList());
    }

    public static OrderItemPO convertPO(@NonNull OrderItem item) {
        OrderItemPO itemPO = new OrderItemPO();
        itemPO.setId(item.getId());
        itemPO.setGoodsName(item.getGoodsName());
        // itemPO.setUpdateTime(item.getUpdateTime());
        itemPO.setOrderId(item.getOrderId());
        itemPO.setGoodsId(item.getGoodsId());
        itemPO.setAmount(item.getAmount());
        itemPO.setPrice(item.getPrice());
        return itemPO;
    }

    public static Aggregate<Order> convert(@NonNull OrderPO orderPO, @NonNull List<OrderItemPO> orderItemPOS) {
        Order order = new Order();
        List<OrderItem> orderItems = orderItemPOS.stream().map(item ->
                new OrderItem(item.getId(), item.getOrderId(), item.getGoodsId(), item.getGoodsName(), item.getAmount(), item.getPrice())
        ).collect(Collectors.toList());
        order.setVersion(orderPO.getVersion());
        order.setDelete(orderPO.getIsDelete());
        order.setConsignee(new Consignee(orderPO.getConsigneeName(), orderPO.getConsigneeShippingAddress(), orderPO.getConsigneeMobile()));
        order.setId(orderPO.getId());
        order.setConsignee(new Consignee(orderPO.getConsigneeName(), orderPO.getConsigneeShippingAddress(), orderPO.getConsigneeMobile()));
        order.setVersion(orderPO.getVersion());
        order.setStatus(orderPO.getStatus());
//        order.setUpdateTime(orderPO.getUpdateTime());
//        order.setCreateTime(orderPO.getCreateTime());
        order.setDeductionPoints(orderPO.getDeductionPoints());
        order.setCouponId(orderPO.getCouponId());
        order.setActualPayMoney(orderPO.getActualPayMoney());
        order.setTotalMoney(orderPO.getTotalMoney());
        order.setOrderSubmitUserId(orderPO.getOrderSubmitUserId());
        order.setDelete(orderPO.getIsDelete());
        order.setOrderItems(orderItems);
        order.setSellerId(orderPO.getSellerId());
        return AggregateFactory.createAggregate(order);
    }
}
