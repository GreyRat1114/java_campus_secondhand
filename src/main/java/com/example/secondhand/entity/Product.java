package com.example.secondhand.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Long sellerId;
    private String status; // PENDING / ON_SALE / REJECTED / SOLD / OFF_SHELF
    private LocalDateTime createTime;

    // 查询列表时额外带出的字段
    private String categoryName;
    private String sellerName;
    private String mainImageUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }

    public String getStatusText() {
        if ("PENDING".equals(status)) return "待审核";
        if ("ON_SALE".equals(status)) return "在售";
        if ("REJECTED".equals(status)) return "审核不通过";
        if ("SOLD".equals(status)) return "已售出";
        if ("OFF_SHELF".equals(status)) return "已下架";
        return status;
    }
}
