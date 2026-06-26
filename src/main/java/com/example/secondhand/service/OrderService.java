package com.example.secondhand.service;

import com.example.secondhand.entity.OrderInfo;
import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.OrderMapper;
import com.example.secondhand.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public OrderService(OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Transactional
    public String createOrder(Long productId, User buyer) {
        if (buyer == null) return "请先登录";
        Product product = productMapper.findById(productId);
        if (product == null) return "商品不存在";
        if (!"ON_SALE".equals(product.getStatus())) return "该商品当前不可购买";
        if (product.getSellerId().equals(buyer.getId())) return "不能购买自己发布的商品";

        OrderInfo activeOrder = orderMapper.findActiveByProductId(productId);
        if (activeOrder != null) return "该商品已有进行中的订单";

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setProductId(productId);
        orderInfo.setBuyerId(buyer.getId());
        orderInfo.setSellerId(product.getSellerId());
        orderInfo.setOrderStatus("WAIT_CONFIRM");
        orderMapper.insert(orderInfo);

        // 下单后先锁定商品，避免被重复购买；取消时会恢复为在售，完成后保持已售出。
        productMapper.updateStatus(productId, "SOLD");
        return null;
    }

    public List<OrderInfo> findBuyerOrders(Long buyerId) {
        return orderMapper.findByBuyerId(buyerId);
    }

    public List<OrderInfo> findSellerOrders(Long sellerId) {
        return orderMapper.findBySellerId(sellerId);
    }

    public List<OrderInfo> findAll(int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return orderMapper.findAll(offset, size);
    }

    public int countAll() {
        return orderMapper.countAll();
    }

    @Transactional
    public String confirm(Long orderId, User user) {
        OrderInfo order = orderMapper.findById(orderId);
        if (order == null) return "订单不存在";
        if (!order.getSellerId().equals(user.getId())) return "只有卖家可以确认订单";
        if (!"WAIT_CONFIRM".equals(order.getOrderStatus())) return "当前订单状态不能确认";
        orderMapper.updateStatus(orderId, "TRADING", false);
        return null;
    }

    @Transactional
    public String finish(Long orderId, User user) {
        OrderInfo order = orderMapper.findById(orderId);
        if (order == null) return "订单不存在";
        if (!order.getBuyerId().equals(user.getId())) return "只有买家可以确认完成";
        if (!"TRADING".equals(order.getOrderStatus())) return "卖家确认后才能完成交易";
        orderMapper.updateStatus(orderId, "FINISHED", true);
        productMapper.updateStatus(order.getProductId(), "SOLD");
        return null;
    }

    @Transactional
    public String cancel(Long orderId, User user) {
        OrderInfo order = orderMapper.findById(orderId);
        if (order == null) return "订单不存在";
        boolean isBuyer = order.getBuyerId().equals(user.getId());
        boolean isSeller = order.getSellerId().equals(user.getId());
        if (!isBuyer && !isSeller) return "只能取消自己的订单";
        if ("FINISHED".equals(order.getOrderStatus())) return "已完成订单不能取消";
        if ("CANCELED".equals(order.getOrderStatus())) return "订单已经取消";
        orderMapper.updateStatus(orderId, "CANCELED", false);
        productMapper.updateStatus(order.getProductId(), "ON_SALE");
        return null;
    }
}
