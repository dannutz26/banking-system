package com.dan.banking.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "spending_limits")
@Data
public class SpendingLimit {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    private LimitPeriod period;

    private BigDecimal limitAmount;
    private BigDecimal currentSpent = BigDecimal.ZERO;
    private LocalDateTime lastReset;
}
