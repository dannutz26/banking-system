package com.dan.banking.backend.repository;

import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerEmail(String email);
    boolean existsByIban(String uniqueIban);
    Optional<Account> findByIban(String iban);
}
