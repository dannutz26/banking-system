package com.dan.banking.repository;

import com.dan.banking.entity.Account;
import com.dan.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerEmail(String email);
    boolean existsByIban(String uniqueIban);
    Optional<Account> findByIban(String iban);
}
