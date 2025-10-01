package org.frogcy.furnitureadmin.product.dto;


import org.frogcy.furniturecommon.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductCreateDTO dto);

    void updateEntityFromDto(ProductUpdateDTO dto, @MappingTarget Product product);

    ProductResponseDTO toDto(Product product);

}
