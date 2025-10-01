package org.frogcy.furnitureadmin.product;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.product.dto.ProductCreateDTO;
import org.frogcy.furnitureadmin.product.dto.ProductResponseDTO;
import org.frogcy.furnitureadmin.product.dto.ProductUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid ProductCreateDTO productDto,
            @RequestPart("images")List<MultipartFile> images
            ){

        ProductResponseDTO responseDTO  = productService.create(productDto, images);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProducts(
            @PathVariable("id") Integer id
    ){
        ProductResponseDTO response = productService.get(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @RequestPart("product") @Valid ProductUpdateDTO productDto,
            @RequestPart("images") @Nullable List<MultipartFile> newImages
    ){
        ProductResponseDTO response = productService.update(productDto, newImages);

        return new ResponseEntity<>(response, HttpStatus.OK)    ;
    }

}
