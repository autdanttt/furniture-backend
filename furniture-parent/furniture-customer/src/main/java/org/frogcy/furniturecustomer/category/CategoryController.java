package org.frogcy.furniturecustomer.category;

import org.frogcy.furniturecustomer.category.dto.CategoryResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> getCategories() {
        List<CategoryResponseDTO> response =  categoryService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
