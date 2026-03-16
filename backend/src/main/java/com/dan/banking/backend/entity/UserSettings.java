package com.dan.banking.backend.entity;

import com.dan.banking.backend.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_settings")
@Data
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String preferredCurrency = "EUR";
    private String primaryAccountIban;
    private boolean darkMode = false;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}