package com.dan.banking.backend.service;

import com.dan.banking.backend.entity.*;
import com.dan.banking.backend.repository.*;
import com.dan.banking.backend.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RememberMeRepository rememberMeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public boolean verifyLogin(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        boolean isSuccess = user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if (isSuccess) {
            auditLogRepository.save(new AuditLog("LOGIN_SUCCESS", "User " + loginRequest.getEmail() + " logged in", "SUCCESS"));
        } else {
            auditLogRepository.save(new AuditLog("LOGIN_FAILURE", "Failed login for " + loginRequest.getEmail(), "FAILURE"));
        }
        return isSuccess;
    }

    @Transactional
    public void createRememberMeToken(String email, HttpServletResponse response) {
        String series = UUID.randomUUID().toString();
        String rawToken = UUID.randomUUID().toString();

        RememberMeToken rmt = new RememberMeToken();
        rmt.setUsername(email);
        rmt.setSeries(series);
        rmt.setToken(passwordEncoder.encode(rawToken));
        rmt.setLastUsed(LocalDateTime.now());

        rememberMeRepository.save(rmt);

        Cookie cookie = new Cookie("remember-me", series + ":" + rawToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) return;

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCountryCode(registerRequest.getCountryCode());

        userRepository.save(user);
        auditLogRepository.save(new AuditLog("USER_REGISTRATION", "Account created for " + registerRequest.getEmail(), "SUCCESS"));
    }

    @Transactional
    public Optional<String> processAutoLogin(String cookieValue, HttpServletResponse response) {
        try {
            String[] parts = cookieValue.split(":");
            String series = parts[0];
            String rawToken = parts[1];

            return rememberMeRepository.findBySeries(series).map(storedToken -> {
                if (!passwordEncoder.matches(rawToken, storedToken.getToken())) {
                    logger.error("SECURITY ALERT: Token mismatch for series {}", series);
                    rememberMeRepository.delete(storedToken); // Delete compromised series
                    return null;
                }

                storedToken.setLastUsed(LocalDateTime.now());

                return storedToken.getUsername();
            });
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void logout(String cookieValue, HttpServletResponse response) {
        if (cookieValue != null && cookieValue.contains(":")) {
            String series = cookieValue.split(":")[0];
            rememberMeRepository.findBySeries(series).ifPresent(token -> {
                rememberMeRepository.delete(token);
                auditLogRepository.save(new AuditLog("LOGOUT", "User " + token.getUsername() + " logged out", "SUCCESS"));
                logger.info("Remember Me token invalidated for series: {}", series);
            });
        }

        Cookie cookie = new Cookie("remember-me", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}