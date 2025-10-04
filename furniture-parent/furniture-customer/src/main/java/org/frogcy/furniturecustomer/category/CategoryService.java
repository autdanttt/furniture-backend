package org.frogcy.furniturecustomer.category;

import org.frogcy.furniturecustomer.category.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDTO> getAll();
}
