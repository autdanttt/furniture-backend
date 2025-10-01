package org.frogcy.furniturecommon.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.BaseEntity;
import org.frogcy.furniturecommon.entity.Category;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 256, nullable = false, unique = true)
    private String name;
    @Column(length = 256, nullable = false, unique = true)
    private String alias;
    @Column(length = 512, nullable = false, name = "short_description")
    private String shortDescription;
    @Column(length = 4096, nullable = false, name = "full_description")
    private String fullDescription;
    private boolean enabled;
    private boolean inStock;
    private Long cost;
    private Long price;
    @Column(name = "discount_percent")
    private Integer discountPercent;
    @Column(name = "final_price")
    private Long finalPrice;
    private float length;
    private float width;
    private float height;
    private float weight;
    @OneToOne
    @JoinColumn(name = "main_image_id")
    private ProductImage mainImage;

    @OrderBy("position ASC")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,orphanRemoval = true)
    List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,orphanRemoval = true)
    List<ProductDetail> details = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    @PreUpdate
    public void updateFinalPrice(){
        if(discountPercent == null || discountPercent <= 0){
            this.finalPrice = price;
        }else {
            this.finalPrice = price - (price * discountPercent / 100);
        }
    }

}
