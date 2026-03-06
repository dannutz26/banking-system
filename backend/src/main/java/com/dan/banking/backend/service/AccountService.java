package com.dan.banking.backend.service;


import com.dan.banking.backend.dto.AccountRequest;
import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.User;
import com.dan.banking.backend.repository.AccountRepository;
import com.dan.banking.backend.repository.UserRepository;
import com.dan.banking.backend.utility.IbanGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final IbanGenerator ibanGenerator;

    public List<Account> getAccounts(String email) {
        return accountRepository.findByOwnerEmail(email);
    }

    public void insertAccount(AccountRequest accountRequest) {
        User owner = userRepository.findByEmail(accountRequest.getOwnerEmail())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + accountRequest.getOwnerEmail()));

        String uniqueIban;
        do {
            String country = (owner.getCountryCode() != null) ? owner.getCountryCode() : "RO";
            uniqueIban = ibanGenerator.generate(country);
        } while (accountRepository.existsByIban(uniqueIban));

        Account account = new Account();
        account.setIban(ibanGenerator.generate(owner.getCountryCode()));
        account.setCurrency(accountRequest.getCurrency().toString());
        account.setOwner(owner);
        accountRepository.save(account);
    }
}
