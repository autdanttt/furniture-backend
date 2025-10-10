package org.frogcy.furnitureadmin.order;

import org.frogcy.furnitureadmin.order.dto.OrderResponseDTO;
import org.frogcy.furnitureadmin.order.dto.OrderSummaryDTO;
import org.frogcy.furnitureadmin.order.dto.UpdateOrderStatusRequest;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;

public interface OrderService {
    PageResponseDTO<OrderSummaryDTO> getOrders(int page, int size, String sortField, String sortDir, String keyword);

    void updateStatus(Integer orderId, UpdateOrderStatusRequest request);

    OrderResponseDTO get(Integer orderId);

    void approveReturn(Integer orderId, String stripeApiKey);

    void rejectReturn(Integer orderId, String reason);
}
