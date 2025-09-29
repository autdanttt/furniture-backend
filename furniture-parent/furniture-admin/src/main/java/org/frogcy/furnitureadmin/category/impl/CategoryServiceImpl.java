package org.frogcy.furnitureadmin.category.impl;

import org.frogcy.furnitureadmin.BadRequestException;
import org.frogcy.furnitureadmin.category.CategoryAlreadyExistsException;
import org.frogcy.furnitureadmin.category.CategoryNotFoundException;
import org.frogcy.furnitureadmin.category.CategoryRepository;
import org.frogcy.furnitureadmin.category.CategoryService;
import org.frogcy.furnitureadmin.category.dto.CategoryCreateDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryMapper;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryUpdateDTO;
import org.frogcy.furnitureadmin.media.AssetService;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.Category;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

        List<CategoryResponseDTO> dtoList = new ArrayList<>();

        for (Category category : categories) {
            dtoList.add(categoryMapper.toDto(category));
        }
        return dtoList;
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

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryResponseDTO update(CategoryUpdateDTO dto, MultipartFile multipartFile) {
        Category category = categoryRepository.findById(dto.getId()).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id " + dto.getId())
        );

        Optional<Category> existingName = categoryRepository.findByName(dto.getName());
        if(existingName.isPresent() && !existingName.get().getId().equals(dto.getId())){
            throw new CategoryAlreadyExistsException("Category already exists with name: " + dto.getName());
        }

        Optional<Category> existingAlias = categoryRepository.findByAlias(dto.getAlias());
        if(existingAlias.isPresent() && !existingAlias.get().getId().equals(dto.getId())){
            throw new CategoryAlreadyExistsException("Category already exists with alias: " + dto.getAlias());
        }

        categoryMapper.updateEntityFromDto(dto, category);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String imageUrl = assetService.uploadToCloudinary(multipartFile, "category");
            category.setImage(imageUrl);
        }

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public void changeEnabled(Integer id, boolean enabled) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id " + id)
        );
        category.setEnabled(enabled);
        categoryRepository.save(category);
    }

    @Override
    public void delete(Integer id, Integer userLoginId) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id " + id)
        );
        category.setDeleted(true);
        category.setDeletedAt(new Date());
        category.setDeletedById(userLoginId);

        categoryRepository.save(category);
    }

    @Override
    public CategoryResponseDTO findById(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id " + id)
        );
        return categoryMapper.toDto(category);
    }

    @Override
    public PageResponseDTO<CategoryResponseDTO> getAllCategory(int page, int size, String sortField, String sortDir, String keyword){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categoryPage = categoryRepository.search(keyword, pageable);

        List<CategoryResponseDTO> categories = categoryPage.getContent().stream()
                .map(categoryMapper::toDto).toList();
        return new PageResponseDTO<>(
                categories,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages()
        );
    }
}
