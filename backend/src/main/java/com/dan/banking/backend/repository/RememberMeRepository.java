package com.dan.banking.backend.repository;

import com.dan.banking.backend.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RememberMeRepository extends JpaRepository<RememberMeToken, Long> {
    Optional<RememberMeToken> findBySeries(String series);
    void deleteByUsername(String username);
}