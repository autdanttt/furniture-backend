package org.frogcy.furnitureadmin.category;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.category.dto.CategoryCreateDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryUpdateDTO;
import org.frogcy.furniturecommon.entity.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCategory(
            @RequestPart(name = "category") @Valid CategoryCreateDTO categoryDto,
            @RequestPart(name = "image") @Nullable MultipartFile multipartFile
            ){
        var response =  categoryService.createCategory(categoryDto, multipartFile);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(
            @RequestPart(name = "category") @Valid CategoryUpdateDTO dto,
            @RequestPart(name = "image") @Nullable MultipartFile multipartFile
            ){
        CategoryResponseDTO responseDTO = categoryService.update(dto, multipartFile);
    }


}
