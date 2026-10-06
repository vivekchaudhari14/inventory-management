package com.project.inventory_management.dto;

import com.project.inventory_management.entity.SupplierStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplierResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private SupplierStatus status;
}
