package com.jimmy.etfquant.service;

import com.jimmy.etfquant.dto.AppUser;
import com.jimmy.etfquant.dto.AuthResponse;
import com.jimmy.etfquant.dto.LoginRequest;
import com.jimmy.etfquant.dto.RegisterRequest;
import com.jimmy.etfquant.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String registerCode;

    public AuthService(
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${auth.register-code}") String registerCode
    ) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.registerCode = registerCode;
    }

    public AuthResponse register(RegisterRequest request) {
        if (!registerCode.equals(request.registration_code())) {
            throw new IllegalArgumentException("Invalid registration code.");
        }

        authRepository.findByPhoneNumber(request.phone_number())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Phone number already exists.");
                });

        String passwordHash = passwordEncoder.encode(request.password());

        Long userId = authRepository.createUser(
                request.phone_number(),
                passwordHash,
                request.user_name()
        );

        String token = jwtService.generateToken(userId, request.phone_number());

        return new AuthResponse(
                userId,
                request.phone_number(),
                request.user_name(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = authRepository.findByPhoneNumber(request.phone_number())
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone number or password."));

        if (!passwordEncoder.matches(request.password(), user.password_hash())) {
            throw new IllegalArgumentException("Invalid phone number or password.");
        }

        authRepository.updateLastLoginTime(user.user_id());

        String token = jwtService.generateToken(user.user_id(), user.phone_number());

        return new AuthResponse(
                user.user_id(),
                user.phone_number(),
                user.user_name(),
                token
        );
    }
}