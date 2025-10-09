package org.frogcy.furnitureadmin.customer.dto;

import org.frogcy.furniturecommon.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponseDTO toDTO(Customer customer);
}
