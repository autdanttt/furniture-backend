package org.frogcy.furniturecustomer.product;

import org.frogcy.furniturecustomer.PageResponseDTO;
import org.frogcy.furniturecustomer.product.dto.ProductResponseDTO;
import org.frogcy.furniturecustomer.product.dto.ProductSummaryDTO;

import java.util.List;

public interface ProductService {
    PageResponseDTO<ProductSummaryDTO> getAllProduct(int page, int size, String sortField, String sortDir, String keyword, Long minPrice, Long maxPrice, Integer categoryId);

    ProductResponseDTO get(Integer id);

    List<ProductSummaryDTO> getLatestProduct();

    List<ProductSummaryDTO> getTopSellingProducts(int days);
}
