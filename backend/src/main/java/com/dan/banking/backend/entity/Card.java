package com.dan.banking.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;

    @Column(nullable = false)
    private String cvv;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "card_type", nullable = false)
    private String cardType;

    @Column(name = "is_blocked")
    private boolean blocked = false;

    @Column(name = "is_disposable")
    private boolean disposable = false;

    @Column(name = "account_id")
    private Long accountId;
}