package com.project.inventory_management.service.impl;

import com.project.inventory_management.dto.ProductRequest;
import com.project.inventory_management.dto.ProductResponse;
import com.project.inventory_management.entity.Category;
import com.project.inventory_management.entity.Product;
import com.project.inventory_management.repository.CategoryRepository;
import com.project.inventory_management.repository.ProductRepository;
import com.project.inventory_management.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Product with SKU already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .unitPrice(request.getUnitPrice())
                .currentStock(request.getCurrentStock())
                .reorderLevel(request.getReorderLevel())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setReorderLevel(request.getReorderLevel());
        product.setCategory(category);

        return mapToResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }

        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {

        return productRepository.findLowStockProducts()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .currentStock(product.getCurrentStock())
                .reorderLevel(product.getReorderLevel())
                .status(product.getStatus())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
    }
}
