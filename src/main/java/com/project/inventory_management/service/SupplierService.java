package com.project.inventory_management.service;

import com.project.inventory_management.dto.SupplierRequest;
import com.project.inventory_management.dto.SupplierResponse;

import java.util.List;

public interface SupplierService {

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse getSupplier(Long id);

    List<SupplierResponse> getAllSuppliers();

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    void deleteSupplier(Long id);
}
