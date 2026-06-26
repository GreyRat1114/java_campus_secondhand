package com.example.secondhand.controller;

import com.example.secondhand.entity.OrderInfo;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/create/{productId}")
    public String create(@PathVariable Long productId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        String error = orderService.createOrder(productId, user);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/products/" + productId;
        }
        redirectAttributes.addFlashAttribute("success", "下单成功，请等待卖家确认");
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        List<OrderInfo> buyerOrders = orderService.findBuyerOrders(user.getId());
        List<OrderInfo> sellerOrders = orderService.findSellerOrders(user.getId());
        model.addAttribute("buyerOrders", buyerOrders);
        model.addAttribute("sellerOrders", sellerOrders);
        return "orders";
    }

    @PostMapping("/orders/{id}/confirm")
    public String confirm(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        String error = orderService.confirm(id, user);
        if (error != null) redirectAttributes.addFlashAttribute("error", error);
        else redirectAttributes.addFlashAttribute("success", "已确认订单，交易进入进行中");
        return "redirect:/orders";
    }

    @PostMapping("/orders/{id}/finish")
    public String finish(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        String error = orderService.finish(id, user);
        if (error != null) redirectAttributes.addFlashAttribute("error", error);
        else redirectAttributes.addFlashAttribute("success", "交易已完成，可以进行评价");
        return "redirect:/orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancel(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        String error = orderService.cancel(id, user);
        if (error != null) redirectAttributes.addFlashAttribute("error", error);
        else redirectAttributes.addFlashAttribute("success", "订单已取消，商品恢复为在售状态");
        return "redirect:/orders";
    }

    private User currentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
