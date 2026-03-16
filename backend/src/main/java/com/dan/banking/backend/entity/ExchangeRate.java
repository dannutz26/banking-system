package com.dan.banking.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_currency", nullable = false)
    private String fromCurrency = "USD";

    @Column(name = "to_currency", nullable = false, unique = true)
    private String toCurrency;

    @Column(nullable = false)
    private Double rate;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
}