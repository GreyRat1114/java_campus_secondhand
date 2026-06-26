package com.example.secondhand.mapper;

import com.example.secondhand.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    int insert(Review review);
    Review findByOrderId(@Param("orderId") Long orderId);
    List<Review> findByTargetUserId(@Param("targetUserId") Long targetUserId);
    List<Review> findByReviewerId(@Param("reviewerId") Long reviewerId);
    List<Review> findAll(@Param("offset") Integer offset, @Param("size") Integer size);
    int countAll();
}
