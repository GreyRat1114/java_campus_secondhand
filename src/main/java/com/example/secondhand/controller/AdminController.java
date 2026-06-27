package com.example.secondhand.controller;

import com.example.secondhand.entity.OrderInfo;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {
    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        return "admin/dashboard";
    }

    @GetMapping("/admin/orders")
    public String orders(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") Integer page,
                         HttpSession session,
                         Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        int size = 10;
        List<OrderInfo> orders = orderService.findAll(page, size);
        int total = orderService.countAll();
        int totalPage = (int) Math.ceil(total * 1.0 / size);
        model.addAttribute("orders", orders);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", Math.max(totalPage, 1));
        return "admin/orders";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}
