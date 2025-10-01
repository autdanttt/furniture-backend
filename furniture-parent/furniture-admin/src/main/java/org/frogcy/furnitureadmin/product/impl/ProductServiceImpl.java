package org.frogcy.furnitureadmin.product.impl;

import org.frogcy.furnitureadmin.category.CategoryNotFoundException;
import org.frogcy.furnitureadmin.category.CategoryRepository;
import org.frogcy.furnitureadmin.category.dto.CategoryMapper;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furnitureadmin.inventory.InventoryService;
import org.frogcy.furnitureadmin.media.AssetService;
import org.frogcy.furnitureadmin.product.*;
import org.frogcy.furnitureadmin.product.dto.*;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.Category;
import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecommon.entity.product.ProductDetail;
import org.frogcy.furniturecommon.entity.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AssetService assetService;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductDetailMapper productDetailMapper;
    private final ProductDetailRepository productDetailRepository;
    private final ProductImageMapper productImageMapper;
    private final CategoryMapper categoryMapper;
    private final InventoryService inventoryService;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, AssetService assetService, ProductImageRepository productImageRepository, CategoryRepository categoryRepository, ProductDetailMapper productDetailMapper, ProductDetailRepository productDetailRepository, ProductImageMapper productImageMapper, CategoryMapper categoryMapper, InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.assetService = assetService;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.productDetailMapper = productDetailMapper;
        this.productDetailRepository = productDetailRepository;
        this.productImageMapper = productImageMapper;
        this.categoryMapper = categoryMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    public ProductResponseDTO create(ProductCreateDTO dto, List<MultipartFile> images) {
        if(productRepository.findByName(dto.getName()).isPresent()){
            throw new ProductAlreadyExistsException("Product already exists with name: " + dto.getName());
        }
        if (productRepository.findByAlias(dto.getAlias()).isPresent()){
            throw new ProductAlreadyExistsException("Product already exists with alias: " + dto.getAlias());
        }

        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + dto.getCategoryId())
        );


        Product product = productMapper.toEntity(dto);
        product.setCategory(category);
        product = productRepository.save(product);

        List<ProductImage> productImages = new ArrayList<>();

        if(images != null && !images.isEmpty()){
            int pos = 1;
            for(MultipartFile image : images){
                String url = assetService.uploadToCloudinary(image, "product");

                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(url);
                productImage.setPosition(pos++);
                productImage.setProduct(product);

                productImages.add(productImage);
            }

            productImageRepository.saveAll(productImages);
            product.setMainImage(productImages.get(0));
            product = productRepository.save(product);
        }

        List<ProductDetail> productDetails = new ArrayList<>();
        if(dto.getDetails() != null && !dto.getDetails().isEmpty()){
            for(ProductDetailCreateDTO dtoDetail : dto.getDetails()){
                ProductDetail productDetail = productDetailMapper.toEntity(dtoDetail);
                productDetail.setProduct(product);
                productDetails.add(productDetail);
            }
            productDetailRepository.saveAll(productDetails);
            product = productRepository.save(product);
        }

        return getProductResponseDTO(product);
    }

    @Override
    public ProductResponseDTO get(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        );

        return getProductResponseDTO(product);
    }

    @Override
    public ProductResponseDTO update(ProductUpdateDTO dto, List<MultipartFile> newImages) {
        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + dto.getId()));

        // check unique name
        productRepository.findByName(dto.getName())
                .filter(p -> !p.getId().equals(dto.getId()))
                .ifPresent(p -> { throw new ProductAlreadyExistsException("Name exists"); });

        // check unique alias
        productRepository.findByAlias(dto.getAlias())
                .filter(p -> !p.getId().equals(dto.getId()))
                .ifPresent(p -> { throw new ProductAlreadyExistsException("Alias exists"); });

        // update common fields
        productMapper.updateEntityFromDto(dto, product);

        // update images
        List<ProductImage> finalImages = getFinalImages(dto, newImages, product);
        if (!finalImages.isEmpty()) {
            // tìm ảnh có position = 1
            ProductImage newMainImage = finalImages.stream()
                    .min(Comparator.comparingInt(ProductImage::getPosition))
                    .orElse(null);

            if (newMainImage != null) {
                product.setMainImage(newMainImage);
            }
        }
        product.getImages().clear();
        product.getImages().addAll(finalImages);

        // update details
        List<ProductDetail> finalDetails = getFinalProductDetail(dto, product);
        product.getDetails().clear();
        product.getDetails().addAll(finalDetails);

        product = productRepository.save(product);
        return getProductResponseDTO(product);
    }

    @Override
    public void delete(Integer id, Integer userLoginId) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        );
        product.setDeleted(true);
        product.setDeletedAt(new Date());
        product.setDeletedById(userLoginId);

        productRepository.save(product);
    }

    @Override
    public void changeEnabled(Integer id, boolean enabled) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        );
        product.setEnabled(enabled);
        productRepository.save(product);
    }

    @Override
    public PageResponseDTO<ProductSummaryDTO> getAllProduct(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productRepository.search(keyword, pageable);


        List<ProductSummaryDTO> productSummaries = products.getContent().stream()
                .map(product -> {
                    ProductSummaryDTO productSummaryDTO = productMapper.toSummary(product);

                    CategoryResponseDTO category = categoryMapper.toDto(product.getCategory());
                    productSummaryDTO.setCategory(category);

                    return productSummaryDTO;
                }).toList();
        return new PageResponseDTO<>(
                productSummaries,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages()
        );
    }


    private List<ProductDetail> getFinalProductDetail(ProductUpdateDTO dto, Product product) {
        List<ProductDetail> finalProductDetail = new ArrayList<>();
        Map<Integer, ProductDetail> currentProductDetailMap = product.getDetails().stream()
                .collect(Collectors.toMap(ProductDetail::getId, Function.identity()));

        if(dto.getRetainedProductDetailIds() != null){
            for(Integer id : dto.getRetainedProductDetailIds()){
                ProductDetail productDetail = currentProductDetailMap.get(id);
                if(productDetail != null){
                    finalProductDetail.add(productDetail);
                }
            }
        }

        if(dto.getNewProductDetails() != null){
            for(int i = 0; i < dto.getNewProductDetails().size(); i++){
                ProductDetail productDetail = productDetailMapper.toEntity(dto.getNewProductDetails().get(i));
                productDetail.setProduct(product);

                finalProductDetail.add(productDetail);
            }
        }

        List<ProductDetail> toRemove = product.getDetails()
                .stream().filter(detail -> !dto.getRetainedProductDetailIds().contains(detail.getId())).toList();

        for (ProductDetail productDetail : toRemove) {
            product.getDetails().remove(productDetail);
        }
        return finalProductDetail;
    }

    private List<ProductImage> getFinalImages(ProductUpdateDTO dto, List<MultipartFile> newImages, Product product) {
        Map<Integer, ProductImage> currentImageMap = product.getImages().stream()
                .collect(Collectors.toMap(ProductImage::getId, Function.identity()));

        List<ProductImage> finalImages = new ArrayList<>();

        if(dto.getRetainedImages() != null){
            for(ImageOrder io : dto.getRetainedImages()){
                ProductImage img = currentImageMap.get(io.getId());
                if(img != null){
                    img.setPosition(io.getPosition());
                    finalImages.add(img);
                }
            }
        }

        LOGGER.info("abs");

        if(newImages != null && !newImages.isEmpty()){
            LOGGER.info("newImages");
            for (int i = 0; i < newImages.size(); i++) {
                MultipartFile file = newImages.get(i);
                LOGGER.info("Uploading image " + i + " to " + file.getOriginalFilename());
                int position = dto.getNewImagesOrder() != null && dto.getNewImagesOrder().size() > i ? dto.getNewImagesOrder().get(i) : (finalImages.size() + 1);
                String url = assetService.uploadToCloudinary(file, "product");

                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(url);
                productImage.setPosition(position);
                productImage.setProduct(product);

                finalImages.add(productImage);
            }
        }


        Set<Integer> retainedIds = dto.getRetainedImages() != null
                ? dto.getRetainedImages().stream().map(ImageOrder::getId).collect(Collectors.toSet())
                : Collections.emptySet();

        List<ProductImage> toRemove = product.getImages()
                .stream().filter(img -> !retainedIds.contains(img.getId()))
                .toList();

        for (ProductImage productImage : toRemove) {
            product.getImages().remove(productImage);
        }

        return finalImages;
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
