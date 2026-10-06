package com.project.inventory_management.service;


import com.project.inventory_management.dto.CategoryRequest;
import com.project.inventory_management.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryRequest request);

    List<Category> getAllCategories();
}
