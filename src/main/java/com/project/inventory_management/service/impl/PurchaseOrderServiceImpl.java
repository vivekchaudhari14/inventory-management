package com.project.inventory_management.service.impl;

import com.project.inventory_management.dto.PurchaseOrderItemRequest;
import com.project.inventory_management.dto.PurchaseOrderItemResponse;
import com.project.inventory_management.dto.PurchaseOrderRequest;
import com.project.inventory_management.dto.PurchaseOrderResponse;
import com.project.inventory_management.entity.*;
import com.project.inventory_management.exception.BadRequestException;
import com.project.inventory_management.exception.ResourceNotFoundException;
import com.project.inventory_management.repository.InventoryTransactionRepository;
import com.project.inventory_management.repository.ProductRepository;
import com.project.inventory_management.repository.PurchaseOrderRepository;
import com.project.inventory_management.repository.SupplierRepository;
import com.project.inventory_management.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl
        implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Override
    public PurchaseOrderResponse createPurchaseOrder(
            PurchaseOrderRequest request) {

        Supplier supplier = supplierRepository.findById(
                request.getSupplierId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Supplier not found"));

        if (supplier.getStatus() == SupplierStatus.INACTIVE) {
            throw new BadRequestException(
                    "Cannot create PO for inactive supplier");
        }

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .orderNumber(generateOrderNumber())
                .supplier(supplier)
                .status(PurchaseOrderStatus.DRAFT)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findById(
                    itemRequest.getProductId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product not found: "
                                    + itemRequest.getProductId()));

            if (product.getStatus() == ProductStatus.INACTIVE) {
                throw new BadRequestException(
                        "Product is inactive: "
                                + product.getSku());
            }

            BigDecimal unitPrice = product.getUnitPrice();

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            );

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();

            purchaseOrder.getItems().add(item);

            totalAmount = totalAmount.add(subtotal);
        }

        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder savedOrder =
                purchaseOrderRepository.save(purchaseOrder);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrder(Long id) {

        PurchaseOrder order =
                purchaseOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase order not found"));

        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse>
    getAllPurchaseOrders() {

        return purchaseOrderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PurchaseOrderResponse approvePurchaseOrder(Long id) {

        PurchaseOrder order =
                purchaseOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase order not found"));

        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BadRequestException(
                    "Only DRAFT purchase orders can be approved");
        }

        order.setStatus(PurchaseOrderStatus.APPROVED);

        return mapToResponse(order);
    }

    @Override
    public PurchaseOrderResponse receivePurchaseOrder(Long id) {

        PurchaseOrder order =
                purchaseOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase order not found"));

        if (order.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new BadRequestException(
                    "Only APPROVED purchase orders can be received");
        }

        for (PurchaseOrderItem item : order.getItems()) {

            Product product = item.getProduct();

            int oldStock = product.getCurrentStock();

            int newStock = oldStock + item.getQuantity();

            product.setCurrentStock(newStock);

            InventoryTransaction transaction =
                    InventoryTransaction.builder()
                            .product(product)
                            .transactionType(
                                    InventoryTransactionType.PURCHASE_RECEIVED)
                            .quantity(item.getQuantity())
                            .referenceType("PURCHASE_ORDER")
                            .referenceId(order.getId())
                            .build();

            inventoryTransactionRepository.save(transaction);
        }

        order.setStatus(PurchaseOrderStatus.RECEIVED);

        return mapToResponse(order);
    }

    @Override
    public PurchaseOrderResponse cancelPurchaseOrder(Long id) {

        PurchaseOrder order =
                purchaseOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase order not found"));

        if (order.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new BadRequestException(
                    "Received purchase order cannot be cancelled");
        }

        if (order.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new BadRequestException(
                    "Purchase order is already cancelled");
        }

        order.setStatus(PurchaseOrderStatus.CANCELLED);

        return mapToResponse(order);
    }

    private String generateOrderNumber() {

        return "PO-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private PurchaseOrderResponse mapToResponse(
            PurchaseOrder order) {

        List<PurchaseOrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                PurchaseOrderItemResponse.builder()
                                        .id(item.getId())
                                        .productId(
                                                item.getProduct().getId())
                                        .productName(
                                                item.getProduct().getName())
                                        .sku(
                                                item.getProduct().getSku())
                                        .quantity(
                                                item.getQuantity())
                                        .unitPrice(
                                                item.getUnitPrice())
                                        .subtotal(
                                                item.getSubtotal())
                                        .build()
                        )
                        .toList();

        return PurchaseOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .supplierId(order.getSupplier().getId())
                .supplierName(order.getSupplier().getName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
