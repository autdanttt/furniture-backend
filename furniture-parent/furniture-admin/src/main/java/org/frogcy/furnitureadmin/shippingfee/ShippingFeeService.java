package org.frogcy.furnitureadmin.shippingfee;

import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeRequestDTO;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeResponseDTO;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeUpdateDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.User;

public interface ShippingFeeService {
    ShippingFeeResponseDTO create(@Valid ShippingFeeRequestDTO dto);

    ShippingFeeResponseDTO update(Integer id, @Valid ShippingFeeUpdateDTO dto);

    void delete(Integer id, User user);

    PageResponseDTO<ShippingFeeResponseDTO> getAllShippingFees(int page, int size, String sortField, String sortDir, String keyword);
}
