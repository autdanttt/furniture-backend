package org.frogcy.furniturecustomer.product;

import org.frogcy.furniturecustomer.PageResponseDTO;
import org.frogcy.furniturecustomer.product.dto.ProductResponseDTO;
import org.frogcy.furniturecustomer.product.dto.ProductSummaryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> listAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer categoryId
    ) {
        PageResponseDTO<ProductSummaryDTO> response = productService.getAllProduct(page, size, sortField, sortDir, keyword, minPrice, maxPrice, categoryId);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        ProductResponseDTO response = productService.get(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestProduct() {
        List<ProductSummaryDTO> response = productService.getLatestProduct();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-selling")
    public ResponseEntity<?> getTopSellingProducts(@RequestParam(defaultValue = "14") int days) {
        List<ProductSummaryDTO> list = productService.getTopSellingProducts(days);
        return ResponseEntity.ok(list);
    }
}
