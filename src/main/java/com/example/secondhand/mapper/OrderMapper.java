package com.example.secondhand.mapper;

import com.example.secondhand.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(OrderInfo orderInfo);

    OrderInfo findById(@Param("id") Long id);

    OrderInfo findActiveByProductId(@Param("productId") Long productId);

    List<OrderInfo> findByBuyerId(@Param("buyerId") Long buyerId);

    List<OrderInfo> findBySellerId(@Param("sellerId") Long sellerId);

    List<OrderInfo> findAll(@Param("offset") Integer offset, @Param("size") Integer size);

    int countAll();

    int updateStatus(@Param("id") Long id,
                     @Param("orderStatus") String orderStatus,
                     @Param("finish") Boolean finish);
}
