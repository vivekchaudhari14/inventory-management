package com.project.inventory_management.service;

import com.project.inventory_management.dto.ProductRequest;
import com.project.inventory_management.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
