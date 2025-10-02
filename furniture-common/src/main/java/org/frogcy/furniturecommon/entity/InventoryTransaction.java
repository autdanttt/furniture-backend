package org.frogcy.furniturecommon.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    private Integer quantityChanged;

    @Enumerated(EnumType.STRING)
    private InventoryTransactionType type;

    private Date transactionDate = new Date();

    private String note;

    @CreatedDate
    private Date createdDate;   // thời điểm insert record vào DB

    @CreatedBy
    @Column(updatable = false)
    private Integer createdById;

}
