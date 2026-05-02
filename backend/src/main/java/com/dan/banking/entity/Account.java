package com.dan.banking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@EqualsAndHashCode(callSuper = false, of = "iban")
public class Account extends BaseEntity {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return iban != null ? iban.equals(account.iban) : account.iban == null;
    }

    @Override
    public int hashCode() {
        return iban != null ? iban.hashCode() : 0;
    }

    @Column(unique = true)
    private String iban;
    private double balance = 0.0;
    private String currency;
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User owner;

    private BigDecimal spendingLimit;
    private BigDecimal currentlySpent = BigDecimal.ZERO;
}
