package com.project.inventory_management.controller;

import com.project.inventory_management.dto.PurchaseOrderRequest;
import com.project.inventory_management.dto.PurchaseOrderResponse;
import com.project.inventory_management.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        purchaseOrderService.createPurchaseOrder(request)
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrder(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>>
    getAllPurchaseOrders() {

        return ResponseEntity.ok(
                purchaseOrderService.getAllPurchaseOrders()
        );
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderResponse>
    approvePurchaseOrder(@PathVariable Long id) {

        return ResponseEntity.ok(
                purchaseOrderService.approvePurchaseOrder(id)
        );
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrderResponse>
    receivePurchaseOrder(@PathVariable Long id) {

        return ResponseEntity.ok(
                purchaseOrderService.receivePurchaseOrder(id)
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrderResponse>
    cancelPurchaseOrder(@PathVariable Long id) {

        return ResponseEntity.ok(
                purchaseOrderService.cancelPurchaseOrder(id)
        );
    }
}
