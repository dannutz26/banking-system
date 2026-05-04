package com.dan.banking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardRequest {
    @NotBlank(message = "IBAN is required")
    private String iban;

    @NotBlank(message = "Card type is required")
    private String type;

    private boolean disposable;
}
