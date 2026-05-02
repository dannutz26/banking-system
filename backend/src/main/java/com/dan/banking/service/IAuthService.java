package com.dan.banking.service;

import com.dan.banking.dto.LoginRequest;
import com.dan.banking.dto.RegisterRequest;
import com.dan.banking.dto.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface IAuthService {
    boolean verifyLogin(LoginRequest loginRequest);
    void registerUser(RegisterRequest registerRequest);
    void createRememberMeToken(String email, HttpServletResponse response);
    UserResponse getCurrentUser(String email);
    Optional<String> processAutoLogin(String cookieValue, HttpServletResponse response);
    void logout(String cookieValue, HttpServletResponse response);
}