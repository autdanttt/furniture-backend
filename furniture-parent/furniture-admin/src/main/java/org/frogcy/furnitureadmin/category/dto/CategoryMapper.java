package org.frogcy.furnitureadmin.category.dto;

import org.frogcy.furniturecommon.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toEntity(CategoryCreateDTO dto);

    void updateEntityFromDto(CategoryUpdateDTO dto, @MappingTarget Category category);

    CategoryResponseDTO toDto(Category category);
}
