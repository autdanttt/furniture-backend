package org.frogcy.furniturecommon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.address.Province;

@Entity
@Table(name = "shipping_fee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFee extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Liên kết đến Province
    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @Column(nullable = false)
    private Long fee;

    private Integer day;
}
