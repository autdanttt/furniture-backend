package org.frogcy.furniturecustomer.customer.dto;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.auth.AuthController;
import org.frogcy.furniturecustomer.auth.dto.CustomerRegisterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "password", ignore = true)
    Customer customerRegisterDtoToCustomer(CustomerRegisterDTO customerRegisterDTO);

    void updateEntityFromDto(CustomerUpdateDTO dto, @MappingTarget Customer customer);

    CustomerResponseDTO toDto(Customer customer);
}
