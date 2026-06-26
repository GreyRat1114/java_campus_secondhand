package com.example.secondhand.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderInfo {
    private Long id;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private String orderStatus; // WAIT_CONFIRM / TRADING / FINISHED / CANCELED
    private LocalDateTime createTime;
    private LocalDateTime finishTime;

    // 列表查询时额外带出的字段
    private String productTitle;
    private BigDecimal productPrice;
    private String productImageUrl;
    private String buyerName;
    private String sellerName;
    private Boolean reviewed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public Boolean getReviewed() { return reviewed; }
    public void setReviewed(Boolean reviewed) { this.reviewed = reviewed; }

    public String getStatusText() {
        if ("WAIT_CONFIRM".equals(orderStatus)) return "待卖家确认";
        if ("TRADING".equals(orderStatus)) return "交易中";
        if ("FINISHED".equals(orderStatus)) return "已完成";
        if ("CANCELED".equals(orderStatus)) return "已取消";
        return orderStatus;
    }
}
