package org.frogcy.furniturecustomer.product.impl;

import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecustomer.PageResponseDTO;
import org.frogcy.furniturecustomer.product.ProductRepository;
import org.frogcy.furniturecustomer.product.ProductService;
import org.frogcy.furniturecustomer.product.dto.ProductMapper;
import org.frogcy.furniturecustomer.product.dto.ProductSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public PageResponseDTO<ProductSummaryDTO> getAllProduct(int page, int size, String sortField, String sortDir, String keyword, Long minPrice, Long maxPrice, Integer categoryId) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("finalPrice"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("finalPrice"), maxPrice));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId));
        }
        spec = spec.and((root, query, cb) ->
                cb.isFalse(root.get("deleted")));

        Page<Product> pageResult = productRepository.findAll(spec, pageable);

        List<ProductSummaryDTO> dtos = pageResult.getContent().stream()
                .map(product -> {
                    ProductSummaryDTO dto = productMapper.toSummaryDTO(product);
                    dto.setMainImageUrl(product.getMainImage().getImageUrl());
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .toList();

        return new PageResponseDTO<>(
                dtos,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    };
}
