package com.example.secondhand.controller;

import com.example.secondhand.entity.OrderInfo;
import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.Review;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.OrderService;
import com.example.secondhand.service.ProductService;
import com.example.secondhand.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ProfileController {
    private final ProductService productService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserMapper userMapper;

    public ProfileController(ProductService productService,
                             OrderService orderService,
                             ReviewService reviewService,
                             UserMapper userMapper) {
        this.productService = productService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.userMapper = userMapper;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = userMapper.findById(sessionUser.getId());
        session.setAttribute("user", user);

        List<Product> myProducts = productService.findBySellerId(user.getId());
        List<OrderInfo> buyerOrders = orderService.findBuyerOrders(user.getId());
        List<OrderInfo> sellerOrders = orderService.findSellerOrders(user.getId());
        List<Review> givenReviews = reviewService.findByReviewerId(user.getId());
        List<Review> receivedReviews = reviewService.findByTargetUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("myProducts", myProducts);
        model.addAttribute("buyerOrders", buyerOrders);
        model.addAttribute("sellerOrders", sellerOrders);
        model.addAttribute("givenReviews", givenReviews);
        model.addAttribute("receivedReviews", receivedReviews);
        return "profile";
    }
}
