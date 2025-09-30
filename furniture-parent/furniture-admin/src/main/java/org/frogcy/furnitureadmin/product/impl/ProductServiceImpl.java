package org.frogcy.furnitureadmin.product.impl;

import org.frogcy.furnitureadmin.category.CategoryNotFoundException;
import org.frogcy.furnitureadmin.category.CategoryRepository;
import org.frogcy.furnitureadmin.media.AssetService;
import org.frogcy.furnitureadmin.product.*;
import org.frogcy.furnitureadmin.product.dto.*;
import org.frogcy.furniturecommon.entity.Category;
import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecommon.entity.product.ProductDetail;
import org.frogcy.furniturecommon.entity.product.ProductImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AssetService assetService;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductDetailMapper productDetailMapper;
    private final ProductDetailRepository productDetailRepository;
    private final ProductImageMapper productImageMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, AssetService assetService, ProductImageRepository productImageRepository, CategoryRepository categoryRepository, ProductDetailMapper productDetailMapper, ProductDetailRepository productDetailRepository, ProductImageMapper productImageMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.assetService = assetService;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.productDetailMapper = productDetailMapper;
        this.productDetailRepository = productDetailRepository;
        this.productImageMapper = productImageMapper;
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
    public ProductResponseDTO update(ProductUpdateDTO productDto, List<MultipartFile> images) {

        return null;
    }

    private ProductResponseDTO getProductResponseDTO(Product product) {
        ProductResponseDTO productResponseDTO = productMapper.toDto(product);
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
