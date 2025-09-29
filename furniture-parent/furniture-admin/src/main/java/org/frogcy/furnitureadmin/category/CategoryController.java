package org.frogcy.furnitureadmin.category;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.category.dto.CategoryCreateDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furnitureadmin.category.dto.CategoryUpdateDTO;
import org.frogcy.furnitureadmin.security.CustomUserDetails;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable("id") Integer id){
        CategoryResponseDTO dto = categoryService.findById(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword
    ) {

        PageResponseDTO<CategoryResponseDTO> response = categoryService.getAllCategory(page, size, sortField, sortDir, keyword);
        return ResponseEntity.ok(response);
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

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeEnabled(@PathVariable("id") Integer id, @RequestParam boolean enabled){

        categoryService.changeEnabled(id, enabled);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userLoginId = null;
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
            userLoginId = user.getUser().getId();
        }
        categoryService.delete(id, userLoginId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
