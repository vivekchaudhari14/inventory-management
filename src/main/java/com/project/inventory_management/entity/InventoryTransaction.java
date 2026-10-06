package com.project.inventory_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_transactions",
        indexes = {
                @Index(name = "idx_inventory_product", columnList = "product_id"),
                @Index(name = "idx_inventory_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryTransactionType transactionType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 50)
    private String referenceType;

    @Column(nullable = false)
    private Long referenceId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
