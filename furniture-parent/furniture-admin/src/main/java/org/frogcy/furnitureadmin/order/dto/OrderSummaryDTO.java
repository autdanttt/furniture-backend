package org.frogcy.furnitureadmin.order.dto;

import lombok.Getter;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.frogcy.furniturecommon.entity.order.PaymentMethod;
import org.frogcy.furniturecommon.entity.order.PaymentStatus;

import java.util.Date;

@Getter
@Setter
public class OrderSummaryDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String provinceName;
    private String wardName;
    private String addressLine;
    private String phoneNumber;
    private String email;
    private String total;
    private Date orderTime;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
}
