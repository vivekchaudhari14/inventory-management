package com.project.inventory_management.repository;

import com.project.inventory_management.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByEmail(String email);

    Optional<Supplier> findByEmail(String email);
}
