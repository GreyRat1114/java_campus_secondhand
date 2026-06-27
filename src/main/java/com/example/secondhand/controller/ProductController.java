package com.example.secondhand.controller;

import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.CategoryMapper;
import com.example.secondhand.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    private final CategoryMapper categoryMapper;

    public ProductController(ProductService productService, CategoryMapper categoryMapper) {
        this.productService = productService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "1") Integer page,
                       Model model) {
        int size = 8;
        List<Product> products = productService.findPage(keyword, categoryId, "ON_SALE", page, size);
        int total = productService.countPage(keyword, categoryId, "ON_SALE");
        int totalPage = (int) Math.ceil(total * 1.0 / size);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryMapper.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", Math.max(totalPage, 1));
        return "products";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            model.addAttribute("error", "商品不存在");
            return "product_detail";
        }
        model.addAttribute("product", product);
        return "product_detail";
    }

    @GetMapping("/products/new")
    public String publishPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("categories", categoryMapper.findAll());
        return "product_form";
    }

    @PostMapping("/products")
    public String publish(Product product,
                          @RequestParam(required = false) MultipartFile image,
                          HttpSession session,
                          Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        product.setSellerId(user.getId());
        String error;
        try {
            error = productService.publish(product, image);
        } catch (IllegalStateException e) {
            error = e.getMessage();
        }
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("categories", categoryMapper.findAll());
            return "product_form";
        }
        model.addAttribute("msg", "发布成功，等待管理员审核");
        return "redirect:/products";
    }
}
