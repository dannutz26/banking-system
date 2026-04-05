package com.dan.banking.backend.repository;

import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountOwnerEmail(String email);
    boolean existsByAccountAndIsBlockedFalse(Account account);
}