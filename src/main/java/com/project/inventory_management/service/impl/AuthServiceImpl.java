package com.project.inventory_management.service.impl;

import com.project.inventory_management.dto.AuthResponse;
import com.project.inventory_management.dto.LoginRequest;
import com.project.inventory_management.dto.RegisterRequest;
import com.project.inventory_management.entity.Role;
import com.project.inventory_management.entity.User;
import com.project.inventory_management.exception.DuplicateResourceException;
import com.project.inventory_management.repository.UserRepository;
import com.project.inventory_management.security.CustomUserDetailsService;
import com.project.inventory_management.security.JwtService;
import com.project.inventory_management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager
            authenticationManager;

    private final CustomUserDetailsService userDetailsService;

    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User with this email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(Role.PURCHASE_MANAGER)
                .build();

        userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid credentials"));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
