package com.project.inventory_management.dto;

import com.project.inventory_management.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private Integer currentStock;
    private Integer reorderLevel;
    private ProductStatus status;
    private Long categoryId;
    private String categoryName;
}
