package org.frogcy.furniturecustomer.order.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.order.PaymentMethod;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String email;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String addressLine;
    @NotNull
    private Integer provinceCode;
    @NotNull
    private String provinceName;
    @NotNull
    private String wardName;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @NotNull
    private Long estimatedShippingFee;
}
