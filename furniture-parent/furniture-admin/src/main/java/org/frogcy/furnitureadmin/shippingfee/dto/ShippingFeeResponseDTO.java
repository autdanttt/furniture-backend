package org.frogcy.furnitureadmin.shippingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeResponseDTO {
    private Integer id;
    private Integer provinceCode;
    private Long fee;
    private Integer day;
    private Boolean deleted;
}
