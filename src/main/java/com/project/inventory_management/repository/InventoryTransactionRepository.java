package com.project.inventory_management.repository;

import com.project.inventory_management.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProductIdOrderByCreatedAtDesc(
            Long productId);
}
