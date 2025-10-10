package org.frogcy.furniturecommon.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.Customer;

import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String paymentIntentId;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();

}
