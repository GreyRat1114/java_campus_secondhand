package com.example.secondhand.mapper;

import com.example.secondhand.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAll();

    List<Category> findAdminAll();

    Category findById(@Param("id") Long id);

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Long id);

    int countProducts(@Param("categoryId") Long categoryId);
}
