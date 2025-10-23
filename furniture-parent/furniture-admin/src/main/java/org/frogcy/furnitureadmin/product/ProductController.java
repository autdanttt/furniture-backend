package org.frogcy.furnitureadmin.product;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furnitureadmin.product.dto.ProductCreateDTO;
import org.frogcy.furnitureadmin.product.dto.ProductResponseDTO;
import org.frogcy.furnitureadmin.product.dto.ProductSummaryDTO;
import org.frogcy.furnitureadmin.product.dto.ProductUpdateDTO;
import org.frogcy.furnitureadmin.security.CustomUserDetails;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'INVENTORY_MANAGER', 'ORDER_MANAGER', 'ASSISTANT')")
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword
    ) {
        PageResponseDTO<ProductSummaryDTO> response = productService.getAllProduct(page, size, sortField, sortDir, keyword);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid ProductCreateDTO productDto,
            @RequestPart("images")List<MultipartFile> images
            ){

        ProductResponseDTO responseDTO  = productService.create(productDto, images);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'INVENTORY_MANAGER', 'ORDER_MANAGER', 'ASSISTANT')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProducts(
            @PathVariable("id") Integer id
    ){
        ProductResponseDTO response = productService.get(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @RequestPart("product") @Valid ProductUpdateDTO productDto,
            @RequestPart("images") @Nullable List<MultipartFile> newImages
    ){
        ProductResponseDTO response = productService.update(productDto, newImages);

        return new ResponseEntity<>(response, HttpStatus.OK)    ;
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userLoginId = null;
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
            userLoginId = user.getUser().getId();
        }
        productService.delete(id, userLoginId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> changeEnabled(@PathVariable("id") Integer id, @RequestParam("enabled") boolean enabled) {
        productService.changeEnabled(id, enabled);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
