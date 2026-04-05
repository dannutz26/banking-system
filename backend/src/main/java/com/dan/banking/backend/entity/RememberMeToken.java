package com.dan.banking.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "persistent_logins")
@Data
public class RememberMeToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String series;
    private String token;
    private LocalDateTime lastUsed;
}