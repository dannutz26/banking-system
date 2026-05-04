package com.dan.banking.repository;

import com.dan.banking.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(value = "SELECT c.* FROM cards c JOIN accounts a ON c.account_id = a.id JOIN users u ON a.owner_id = u.id WHERE u.email = :email", nativeQuery = true)
    List<Card> findByEmail(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) > 0 FROM cards WHERE account_id = :accountId AND is_blocked = false AND is_disposable = false", nativeQuery = true)
    Boolean hasActiveCard(@Param("accountId") Long accountId);

    @Query(value = "SELECT c.* FROM cards c JOIN accounts a ON c.account_id = a.id JOIN users u ON a.owner_id = u.id WHERE c.id = :cardId AND u.email = :email", nativeQuery = true)
    Optional<Card> findByIdAndOwnerEmail(@Param("cardId") Long cardId, @Param("email") String email);
}
