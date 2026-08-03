package com.dan.banking.repository;

import com.dan.banking.entity.Account;
import com.dan.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerEmail(String email);
    boolean existsByIban(String uniqueIban);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByIban(String iban);
    Optional<Account> findByIbanAndOwnerEmail(String iban, String email);
}
