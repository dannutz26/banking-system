package com.dan.banking.backend.service;

import com.dan.banking.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional(readOnly = true)
    public boolean verifyLogin(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> user.getPassword().equals(loginRequest.getPassword()))
                .orElse(false);
    }

    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new RuntimeException("User with this email already exists!");

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        userRepository.save(user);
    }
}