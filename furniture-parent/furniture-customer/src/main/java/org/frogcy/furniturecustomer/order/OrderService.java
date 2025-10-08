package org.frogcy.furniturecustomer.order;


import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.order.dto.OrderRequestDTO;
import org.frogcy.furniturecustomer.order.dto.OrderResponseDTO;
import org.frogcy.furniturecustomer.order.dto.OrderResultDTO;
import org.frogcy.furniturecustomer.order.dto.OrderSummaryDTO;

import java.util.List;

public interface OrderService {
    OrderResultDTO create(Customer customer, @Valid OrderRequestDTO dto);

    OrderSummaryDTO summaryOrder(Customer customer);

    OrderResponseDTO get(Customer customer, Integer id);

    List<OrderResponseDTO> getAll(Customer customer);
}
