package org.frogcy.furniturecustomer.order;


import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.order.PaymentStatus;
import org.frogcy.furniturecustomer.order.dto.*;

import java.util.List;

public interface OrderService {
    OrderResultDTO create(Customer customer, @Valid OrderRequestDTO dto);

    OrderSummaryDTO summaryOrder(Customer customer);

    OrderResponseDTO get(Customer customer, Integer id);

    List<OrderResponseDTO> getAll(Customer customer);

    void cancelOrder(Customer customer, Integer id);

    List<OrderTrackResponseDTO> getOrderTracking(Integer orderId, Customer customer);

    void updatePaymentStatus(String orderId,String paymentIntentId, PaymentStatus paymentStatus);

    void updateRefundSuccess(String paymentIntentId, PaymentStatus paymentStatus);
}
