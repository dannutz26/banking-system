package com.dan.banking.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String details;
    private String status;
    private LocalDateTime createdAt;

    public AuditLog(String action, String details, String status) {
        this.action = action;
        this.details = details;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}