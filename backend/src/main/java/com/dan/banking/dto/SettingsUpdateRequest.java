package com.dan.banking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettingsUpdateRequest {
    @NotBlank(message = "Preferred currency is required")
    private String preferredCurrency;
    private String primaryAccountIban;
    private Boolean darkMode;
}
