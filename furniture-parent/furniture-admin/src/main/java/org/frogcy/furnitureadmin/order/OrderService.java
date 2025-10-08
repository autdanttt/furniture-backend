package org.frogcy.furnitureadmin.order;

import org.frogcy.furnitureadmin.order.dto.OrderSummaryDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;

public interface OrderService {
    PageResponseDTO<OrderSummaryDTO> getOrders(int page, int size, String sortField, String sortDir, String keyword);
}
