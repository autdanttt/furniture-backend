package org.frogcy.furniturecustomer.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.order.OrderDetail;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.frogcy.furniturecommon.entity.order.PaymentMethod;
import org.frogcy.furniturecommon.entity.order.PaymentStatus;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponseDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String provinceName;
    private String wardName;
    private Date orderTime;
    private String addressLine;
    private String phoneNumber;
    private String email;
    private Long shippingCost;
    private Long productCost;
    private Long subtotal;
    private Long total;
    private int deliverDays;
    private Date deliverDate;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Set<OrderDetailDTO> details;
}
