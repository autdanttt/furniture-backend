package org.frogcy.furnitureadmin.category;

import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.category.dto.CategoryCreateDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryUpdateDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDTO> getCategories();

    CategoryResponseDTO createCategory(CategoryCreateDTO dto, MultipartFile multipartFile);

    CategoryResponseDTO update(@Valid CategoryUpdateDTO dto, MultipartFile multipartFile);

    void changeEnabled(Integer id, boolean enabled);

    void delete(Integer id, Integer userLoginId);

    CategoryResponseDTO findById(Integer id);

    PageResponseDTO<CategoryResponseDTO> getAllCategory(int page, int size, String sortField, String sortDir, String keyword);
}
