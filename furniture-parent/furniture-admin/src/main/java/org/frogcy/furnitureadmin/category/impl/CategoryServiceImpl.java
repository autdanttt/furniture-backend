package org.frogcy.furnitureadmin.category.impl;

import org.frogcy.furnitureadmin.category.CategoryAlreadyExistsException;
import org.frogcy.furnitureadmin.category.CategoryNotFoundException;
import org.frogcy.furnitureadmin.category.CategoryRepository;
import org.frogcy.furnitureadmin.category.CategoryService;
import org.frogcy.furnitureadmin.category.dto.CategoryCreateDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryMapper;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryUpdateDTO;
import org.frogcy.furnitureadmin.media.AssetService;
import org.frogcy.furniturecommon.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final AssetService assetService;
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, AssetService assetService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.assetService = assetService;
    }

    @Override
    public List<CategoryResponseDTO> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        // map id -> entity
        Map<Integer, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        // lấy root categories (cha null)
        List<Category> rootEntities = categories.stream()
                .filter(c -> c.getParent() == null)
                .toList();

        // convert sang dto có đệ quy
        return rootEntities.stream()
                .map(cat -> buildCategoryTree(cat, categoryMap))
                .toList();
    }
    private CategoryResponseDTO buildCategoryTree(Category cat, Map<Integer, Category> all) {
        CategoryResponseDTO dto = categoryMapper.toDto(cat);
        List<CategoryResponseDTO> childDtos = all.values().stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(cat.getId()))
                .map(c -> buildCategoryTree(c, all))
                .toList();
        dto.setChildren(childDtos);
        return dto;
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryCreateDTO dto, MultipartFile multipartFile) {
        if(categoryRepository.findByName(dto.getName()).isPresent()){
            throw new CategoryAlreadyExistsException("Category already exists with name: " + dto.getName());
        }
        if(categoryRepository.findByAlias(dto.getAlias()).isPresent()){
            throw new CategoryAlreadyExistsException("Category already exists with alias: " + dto.getAlias());
        }
        String categoryImage = "";
        if(multipartFile != null){
            categoryImage = assetService.uploadToCloudinary(multipartFile, "category");
        }
        Category category = categoryMapper.toEntity(dto);
        category.setImage(categoryImage);

       if(dto.getParentId() != null){
           Category parent = categoryRepository.findById(dto.getParentId()).orElseThrow(
                   () -> new CategoryNotFoundException("Category not found with parent id" + dto.getParentId())
           );
           category.setParent(parent);
       }

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryResponseDTO update(CategoryUpdateDTO dto, MultipartFile multipartFile) {
        Category category = categoryRepository.findById(dto.getId()).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id " + dto.getId())
        );

        Optional<Category> existingName = categoryRepository
        return null;
    }
}
