package com.example.secondhand.controller;

import com.example.secondhand.entity.OrderInfo;
import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.OrderService;
import com.example.secondhand.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {
    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        List<Product> pendingProducts = productService.findPage(null, null, "PENDING", 1, 20);
        List<OrderInfo> recentOrders = orderService.findAll(1, 10);
        model.addAttribute("pendingProducts", pendingProducts);
        model.addAttribute("recentOrders", recentOrders);
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

    @PostMapping("/admin/products/{id}/approve")
    public String approve(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        productService.approve(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/products/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        productService.reject(id);
        return "redirect:/admin";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}
