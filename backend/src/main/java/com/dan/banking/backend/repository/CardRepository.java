package com.dan.banking.backend.repository;

import com.dan.banking.backend.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(value = "SELECT c.* FROM cards c JOIN accounts a ON c.account_id = a.id JOIN users u ON a.owner_id = u.id WHERE u.email = :email", nativeQuery = true)
    List<Card> findByEmail(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) > 0 FROM cards WHERE account_id = :accountId AND is_blocked = false AND is_disposable = false", nativeQuery = true)
    Integer hasActiveCard(@Param("accountId") Long accountId);
}