package org.frogcy.furniturecustomer.product.impl;

import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecommon.entity.product.ProductImage;
import org.frogcy.furniturecustomer.PageResponseDTO;
import org.frogcy.furniturecustomer.inventory.InventoryService;
import org.frogcy.furniturecustomer.product.*;
import org.frogcy.furniturecustomer.product.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductDetailMapper productDetailMapper;
    private final ProductImageRepository productImageRepository;
    private final ProductDetailRepository productDetailRepository;
    private final InventoryService inventoryService;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, ProductImageMapper productImageMapper, ProductDetailMapper productDetailMapper, ProductDetailRepository productDetailRepository, ProductImageRepository productImageRepository, InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productImageMapper = productImageMapper;
        this.productDetailMapper = productDetailMapper;
        this.productDetailRepository = productDetailRepository;
        this.productImageRepository = productImageRepository;
        this.inventoryService = inventoryService;
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
    }

    @Override
    public ProductResponseDTO get(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        );

        return getProductResponseDTO(product);
    }

    private ProductResponseDTO getProductResponseDTO(Product product) {
        ProductResponseDTO productResponseDTO = productMapper.toDto(product);

        productResponseDTO.setInStock(inventoryService.inStock(product));

        List<ProductImageDTO> productImageDTOs = new ArrayList<>();
        List<ProductImage> listProductImages = productImageRepository.findAllByProductId(product.getId());
        listProductImages.sort(Comparator.comparing(ProductImage::getPosition));
        for (ProductImage productImage : listProductImages) {
            ProductImageDTO productImageDTO = productImageMapper.toDTO(productImage);
            productImageDTOs.add(productImageDTO);
        }
        productResponseDTO.setImages(productImageDTOs);


        List<ProductDetailDTO> productDetailDTOs = productDetailRepository.findAllByProductId(product.getId())
                .stream().map(productDetailMapper::toDTO).toList();

        productResponseDTO.setDetails(productDetailDTOs);

        return productResponseDTO;
    }
}
