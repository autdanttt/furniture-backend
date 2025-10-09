package org.frogcy.furnitureadmin.shippingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeRequestDTO {
    private Integer provinceCode;
    private Long fee;
    private Integer day;
}
