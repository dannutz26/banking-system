package com.dan.banking.controller;

import com.dan.banking.dto.SettingsUpdateRequest;
import com.dan.banking.entity.User;
import com.dan.banking.entity.UserSettings;
import com.dan.banking.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserSettings> getSettings(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getSettings() == null) {
            UserSettings defaultSettings = new UserSettings();
            defaultSettings.setUser(user);
            user.setSettings(defaultSettings);
            userRepository.save(user);
        }

        return ResponseEntity.ok(user.getSettings());
    }

    @PutMapping("/update")
    @Transactional
    public ResponseEntity<String> updateSettings(@Valid @RequestBody SettingsUpdateRequest payload,
                                                 Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserSettings settings = user.getSettings();
        if (settings == null) {
            settings = new UserSettings();
            settings.setUser(user);
            user.setSettings(settings);
        }

        settings.setPreferredCurrency(payload.getPreferredCurrency());
        settings.setPrimaryAccountIban(payload.getPrimaryAccountIban());

        userRepository.save(user);
        return ResponseEntity.ok("Settings saved successfully");
    }
}
