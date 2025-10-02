package org.frogcy.furniturecommon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "customer_refresh_tokens")
@Getter
@Setter
public class CustomerRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length=256)
    private String token;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Date expiryTime;
}
