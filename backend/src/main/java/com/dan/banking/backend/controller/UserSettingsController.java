package com.dan.banking.backend.controller;

import com.dan.banking.backend.entity.User;
import com.dan.banking.backend.entity.UserSettings;
import com.dan.banking.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserSettings> getSettings(@RequestParam String email) {
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

    @PostMapping("/update")
    @Transactional
    public ResponseEntity<?> updateSettings(
            @RequestParam String email,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserSettings settings = user.getSettings();
            if (settings == null) {
                settings = new UserSettings();
                settings.setUser(user);
                user.setSettings(settings);
            }

            settings.setPreferredCurrency((String) payload.get("preferredCurrency"));
            settings.setPrimaryAccountIban((String) payload.get("primaryAccountIban"));

            userRepository.save(user);

            return ResponseEntity.ok("Settings saved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
