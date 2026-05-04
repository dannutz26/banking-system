package com.dan.banking.service;

import com.dan.banking.dto.LoginRequest;
import com.dan.banking.dto.RegisterRequest;
import com.dan.banking.dto.UserResponse;

public interface IAuthService {
    boolean verifyLogin(LoginRequest loginRequest);
    void registerUser(RegisterRequest registerRequest);
    String generateToken(String email);
    UserResponse getCurrentUser(String email);
}