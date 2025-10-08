package org.frogcy.furniturecommon.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.product.Product;

@Entity
@Table(name = "order_details")
@Getter
@Setter
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer quantity;
    private Long productCost;
    private Long unitPrice;
    private Long subtotal;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
