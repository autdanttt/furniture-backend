package org.frogcy.furniturecustomer.product;

import org.frogcy.furniturecustomer.PageResponseDTO;
import org.frogcy.furniturecustomer.product.dto.ProductSummaryDTO;

public interface ProductService {
    PageResponseDTO<ProductSummaryDTO> getAllProduct(int page, int size, String sortField, String sortDir, String keyword, Long minPrice, Long maxPrice, Integer categoryId);
}
