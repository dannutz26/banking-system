package com.dan.banking.repository;

import com.dan.banking.entity.Account;
import com.dan.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountInOrderByTimestampDesc(List<Account> accounts);
}
