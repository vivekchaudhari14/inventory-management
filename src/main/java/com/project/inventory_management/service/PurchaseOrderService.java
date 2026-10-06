package com.project.inventory_management.service;

import com.project.inventory_management.dto.PurchaseOrderRequest;
import com.project.inventory_management.dto.PurchaseOrderResponse;

import java.util.List;

public interface PurchaseOrderService {

    PurchaseOrderResponse createPurchaseOrder(
            PurchaseOrderRequest request);

    PurchaseOrderResponse getPurchaseOrder(Long id);

    List<PurchaseOrderResponse> getAllPurchaseOrders();

    PurchaseOrderResponse approvePurchaseOrder(Long id);

    PurchaseOrderResponse receivePurchaseOrder(Long id);

    PurchaseOrderResponse cancelPurchaseOrder(Long id);
}
