package com.example.secondhand.mapper;

import com.example.secondhand.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> findPage(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("status") String status,
                           @Param("offset") Integer offset,
                           @Param("size") Integer size);

    int countPage(@Param("keyword") String keyword,
                  @Param("categoryId") Long categoryId,
                  @Param("status") String status);

    Product findById(@Param("id") Long id);
    List<Product> findBySellerId(@Param("sellerId") Long sellerId);
    int insert(Product product);
    int insertImage(@Param("productId") Long productId, @Param("imageUrl") String imageUrl);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
