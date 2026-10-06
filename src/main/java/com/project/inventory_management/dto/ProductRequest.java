package com.project.inventory_management.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 50)
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than zero")
    private BigDecimal unitPrice;

    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer currentStock;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
