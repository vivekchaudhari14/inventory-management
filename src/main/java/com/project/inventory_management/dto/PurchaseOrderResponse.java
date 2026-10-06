package com.project.inventory_management.dto;

import com.project.inventory_management.entity.PurchaseOrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PurchaseOrderResponse {

    private Long id;
    private String orderNumber;

    private Long supplierId;
    private String supplierName;

    private PurchaseOrderStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private List<PurchaseOrderItemResponse> items;
}
