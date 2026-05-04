package com.dan.banking.service;

import com.dan.banking.config.JwtService;
import com.dan.banking.entity.User;
import com.dan.banking.repository.AuditLogRepository;
import com.dan.banking.repository.UserRepository;
import com.dan.banking.dto.LoginRequest;
import com.dan.banking.dto.RegisterRequest;
import com.dan.banking.dto.UserResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public boolean verifyLogin(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        boolean isSuccess = user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if (isSuccess) {
            auditLogRepository.save(new com.dan.banking.entity.AuditLog("LOGIN_SUCCESS", "User " + loginRequest.getEmail() + " logged in", "SUCCESS"));
        } else {
            auditLogRepository.save(new com.dan.banking.entity.AuditLog("LOGIN_FAILURE", "Failed login for " + loginRequest.getEmail(), "FAILURE"));
        }
        return isSuccess;
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCountryCode(registerRequest.getCountryCode());

        userRepository.save(user);
        auditLogRepository.save(new com.dan.banking.entity.AuditLog("USER_REGISTRATION", "Account created for " + registerRequest.getEmail(), "SUCCESS"));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    @Override
    public String generateToken(String email) {
        return jwtService.generateToken(email);
    }
}
