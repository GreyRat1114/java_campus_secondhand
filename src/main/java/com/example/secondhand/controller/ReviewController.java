package com.example.secondhand.controller;

import com.example.secondhand.entity.OrderInfo;
import com.example.secondhand.entity.Review;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/new/{orderId}")
    public String reviewForm(@PathVariable Long orderId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            OrderInfo order = reviewService.getReviewOrder(orderId, currentUser(session));
            model.addAttribute("order", order);
            return "review_form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders";
        }
    }

    @PostMapping("/reviews/create/{orderId}")
    public String createReview(@PathVariable Long orderId,
                               @RequestParam Integer score,
                               @RequestParam(required = false) String content,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            reviewService.createReview(orderId, score, content, currentUser(session));
            redirectAttributes.addFlashAttribute("success", "评价成功，卖家信用分已更新");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/admin/reviews")
    public String adminReviews(@RequestParam(defaultValue = "1") Integer page, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        int size = 10;
        List<Review> reviews = reviewService.findAll(page, size);
        int total = reviewService.countAll();
        int totalPage = (int) Math.ceil(total * 1.0 / size);
        model.addAttribute("reviews", reviews);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", Math.max(totalPage, 1));
        return "admin/reviews";
    }

    private User currentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    private boolean isAdmin(HttpSession session) {
        User user = currentUser(session);
        return user != null && "ADMIN".equals(user.getRole());
    }
}
