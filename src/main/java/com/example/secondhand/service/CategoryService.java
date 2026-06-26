package com.example.secondhand.service;

import com.example.secondhand.entity.Category;
import com.example.secondhand.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> findAdminAll() {
        return categoryMapper.findAdminAll();
    }

    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }

    @Transactional
    public void create(String name, String description) {
        validate(name);

        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description);

        categoryMapper.insert(category);
    }

    @Transactional
    public void update(Long id, String name, String description) {
        validate(name);

        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }

        category.setName(name.trim());
        category.setDescription(description);

        categoryMapper.update(category);
    }

    @Transactional
    public void delete(Long id) {
        int count = categoryMapper.countProducts(id);
        if (count > 0) {
            throw new IllegalArgumentException("该分类下已有商品，不能删除");
        }
        categoryMapper.deleteById(id);
    }

    private void validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("分类名称不能超过50字");
        }
    }
}
