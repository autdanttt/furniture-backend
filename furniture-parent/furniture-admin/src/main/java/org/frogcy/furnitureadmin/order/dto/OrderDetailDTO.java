package org.frogcy.furnitureadmin.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private Long price;
    private Integer quantity;
}
