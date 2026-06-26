package com.example.secondhand.service;

import com.example.secondhand.entity.OrderInfo;
import com.example.secondhand.entity.Review;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.OrderMapper;
import com.example.secondhand.mapper.ReviewMapper;
import com.example.secondhand.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public ReviewService(ReviewMapper reviewMapper, OrderMapper orderMapper, UserMapper userMapper) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    public OrderInfo getReviewOrder(Long orderId, User user) {
        OrderInfo order = orderMapper.findById(orderId);
        checkReviewable(order, user);
        return order;
    }

    @Transactional
    public void createReview(Long orderId, Integer score, String content, User user) {
        OrderInfo order = orderMapper.findById(orderId);
        checkReviewable(order, user);

        if (score == null || score < 1 || score > 5) {
            throw new IllegalArgumentException("评分必须在1到5分之间");
        }
        if (content != null && content.length() > 500) {
            throw new IllegalArgumentException("评价内容不能超过500字");
        }

        Review review = new Review();
        review.setOrderId(orderId);
        review.setReviewerId(user.getId());
        review.setTargetUserId(order.getSellerId());
        review.setScore(score);
        review.setContent(content);
        reviewMapper.insert(review);

        userMapper.updateCreditScore(order.getSellerId(), calculateCreditDelta(score));
    }

    private void checkReviewable(OrderInfo order, User user) {
        if (user == null) throw new IllegalArgumentException("请先登录");
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!order.getBuyerId().equals(user.getId())) throw new IllegalArgumentException("只有买家可以评价卖家");
        if (!"FINISHED".equals(order.getOrderStatus())) throw new IllegalArgumentException("订单完成后才能评价");
        if (reviewMapper.findByOrderId(order.getId()) != null) throw new IllegalArgumentException("该订单已经评价过了");
    }

    private int calculateCreditDelta(Integer score) {
        if (score == 5) return 2;
        if (score == 4) return 1;
        if (score == 3) return 0;
        if (score == 2) return -1;
        return -3;
    }

    public List<Review> findByTargetUserId(Long targetUserId) {
        return reviewMapper.findByTargetUserId(targetUserId);
    }

    public List<Review> findByReviewerId(Long reviewerId) {
        return reviewMapper.findByReviewerId(reviewerId);
    }

    public List<Review> findAll(int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return reviewMapper.findAll(offset, size);
    }

    public int countAll() {
        return reviewMapper.countAll();
    }
}
