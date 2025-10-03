package org.frogcy.furniturecommon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpType type;

    private Date expiresAt;

    private Date createdAt;
    private boolean used = false;


    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;


}

