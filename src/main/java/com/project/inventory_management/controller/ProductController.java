package com.project.inventory_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping("/test")
    public String test() {
        System.out.println("test");
        return "Inventory Management System is running";
    }
}
