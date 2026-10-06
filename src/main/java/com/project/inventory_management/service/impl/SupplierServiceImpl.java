package com.project.inventory_management.service.impl;

import com.project.inventory_management.dto.SupplierRequest;
import com.project.inventory_management.dto.SupplierResponse;
import com.project.inventory_management.entity.Supplier;
import com.project.inventory_management.repository.SupplierRepository;
import com.project.inventory_management.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {

        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Supplier with this email already exists");
        }

        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        return mapToResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplier(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        return mapToResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {

        return supplierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SupplierResponse updateSupplier(
            Long id,
            SupplierRequest request) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());

        return mapToResponse(supplier);
    }

    @Override
    public void deleteSupplier(Long id) {

        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found");
        }

        supplierRepository.deleteById(id);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {

        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .status(supplier.getStatus())
                .build();
    }
}