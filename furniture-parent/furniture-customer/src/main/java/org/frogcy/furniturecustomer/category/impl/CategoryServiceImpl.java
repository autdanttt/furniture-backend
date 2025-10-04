package org.frogcy.furniturecustomer.category.impl;

import org.frogcy.furniturecommon.entity.Category;
import org.frogcy.furniturecustomer.category.CategoryRepository;
import org.frogcy.furniturecustomer.category.CategoryService;
import org.frogcy.furniturecustomer.category.dto.CategoryMapper;
import org.frogcy.furniturecustomer.category.dto.CategoryResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponseDTO> getAll() {
        List<Category> categories = categoryRepository.findAllByEnabledIsTrueAndDeletedIsFalse();
        return categories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }
}
