package com.dan.banking.backend.service;

import com.dan.banking.backend.entity.AuditLog;
import com.dan.banking.backend.repository.AuditLogRepository;
import com.dan.banking.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dan.banking.backend.dto.LoginRequest;
import com.dan.banking.backend.dto.RegisterRequest;
import com.dan.banking.backend.entity.User;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public boolean verifyLogin(LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        boolean isSuccess = userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> user.getPassword().equals(loginRequest.getPassword()))
                .orElse(false);

        if (isSuccess) {
            auditLogRepository.save(new AuditLog("LOGIN_SUCCESS", "User " + loginRequest.getEmail() + " logged in", "SUCCESS"));
        } else {
            logger.warn("FAILED LOGIN attempt for email: {}", loginRequest.getEmail());
            auditLogRepository.save(new AuditLog("LOGIN_FAILURE", "Invalid credentials for email: " + loginRequest.getEmail(), "FAILURE"));
        }

        return isSuccess;
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("Registration failed: Email {} already exists", registerRequest.getEmail());
            auditLogRepository.save(new AuditLog("REGISTER_FAILURE", "Email already exists: " + registerRequest.getEmail(), "FAILURE"));
            return;
            // throw new BankingException("User with this email already exists!", "DUPLICATE_EMAIL");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setCountryCode(registerRequest.getCountryCode());

        userRepository.save(user);

        logger.info("New user registered: {}", registerRequest.getEmail());
        auditLogRepository.save(new AuditLog("USER_REGISTRATION", "Account created for " + registerRequest.getEmail(), "SUCCESS"));
    }
}