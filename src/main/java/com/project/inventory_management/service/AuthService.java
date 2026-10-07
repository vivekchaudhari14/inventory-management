package com.project.inventory_management.service;


import com.project.inventory_management.dto.AuthResponse;
import com.project.inventory_management.dto.LoginRequest;
import com.project.inventory_management.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
