package com.example.secondhand.service;

import com.example.secondhand.config.UploadPaths;
import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> findPage(String keyword, Long categoryId, String status, int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return productMapper.findPage(keyword, categoryId, status, offset, size);
    }

    public int countPage(String keyword, Long categoryId, String status) {
        return productMapper.countPage(keyword, categoryId, status);
    }

    public List<Product> findAdminPage(String keyword, Long categoryId, String status, int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return productMapper.findAdminPage(keyword, categoryId, status, offset, size);
    }

    public int countAdminPage(String keyword, Long categoryId, String status) {
        return productMapper.countAdminPage(keyword, categoryId, status);
    }

    public Product findById(Long id) {
        return productMapper.findById(id);
    }

    public List<Product> findBySellerId(Long sellerId) {
        return productMapper.findBySellerId(sellerId);
    }

    public List<Product> findRecommendProducts(User user, int size) {
        Long userId = user == null ? null : user.getId();
        if (userId != null) {
            List<Product> recommended = productMapper.findRecommendedByBuyer(userId, size);
            if (recommended != null && !recommended.isEmpty()) {
                return recommended;
            }
        }
        return productMapper.findFallbackRecommendations(userId, size);
    }

    @Transactional
    public String publish(Product product, MultipartFile image) {
        if (!StringUtils.hasText(product.getTitle())) return "商品标题不能为空";
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) return "价格必须大于 0";
        if (product.getCategoryId() == null) return "请选择商品分类";

        product.setStatus("PENDING"); // 发布后先待审核
        productMapper.insert(product);

        if (image != null && !image.isEmpty()) {
            String url = saveImage(image);
            productMapper.insertImage(product.getId(), url);
        }
        return null;
    }

    private String saveImage(MultipartFile image) {
        String original = image.getOriginalFilename();
        String suffix = "";
        if (original != null && original.contains(".")) {
            suffix = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
        IOException lastIoException = null;
        SecurityException lastSecurityException = null;

        for (Path root : UploadPaths.uploadRoots()) {
            Path dir = root.resolve("product").toAbsolutePath().normalize();
            Path target = dir.resolve(filename);
            try (InputStream inputStream = image.getInputStream()) {
                Files.createDirectories(dir);
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
                return "/uploads/product/" + filename;
            } catch (IOException e) {
                lastIoException = e;
            } catch (SecurityException e) {
                lastSecurityException = e;
            }
        }

        if (lastIoException != null) {
            throw new IllegalStateException("图片上传失败，请检查上传目录权限后重试", lastIoException);
        }
        if (lastSecurityException != null) {
            throw new IllegalStateException("图片上传失败，请检查上传目录权限后重试", lastSecurityException);
        }
        throw new IllegalStateException("图片上传失败，请检查图片文件后重试");
    }

    public void approve(Long id) {
        approveProduct(id);
    }

    public void reject(Long id) {
        rejectProduct(id);
    }

    public void approveProduct(Long id) {
        productMapper.updateStatus(id, "ON_SALE");
    }

    public void rejectProduct(Long id) {
        productMapper.updateStatus(id, "REJECTED");
    }

    public void offShelfProduct(Long id) {
        productMapper.updateStatus(id, "OFF_SHELF");
    }
}
