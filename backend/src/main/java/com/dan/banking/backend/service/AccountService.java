package com.dan.banking.backend.service;


import com.dan.banking.backend.dto.AccountRequest;
import com.dan.banking.backend.dto.TransferRequest;
import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.Transaction;
import com.dan.banking.backend.entity.User;
import com.dan.banking.backend.repository.AccountRepository;
import com.dan.banking.backend.repository.TransactionRepository;
import com.dan.banking.backend.repository.UserRepository;
import com.dan.banking.backend.utility.IbanGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final IbanGenerator ibanGenerator;
    private final CurrencyService currencyService;
    private final TransactionRepository transactionRepository;

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

    @Transactional
    public void transferMoney(TransferRequest request) {
        Account source = findAccountByIban(request.getFromIban());
        Account target = findAccountByIban(request.getToIban());

        validateTransfer(source, request);

        double amountToDebit = request.getAmount();
        double amountToCredit = calculateCreditAmount(source, target, amountToDebit);

        updateBalances(source, target, amountToDebit, amountToCredit);

        createTransactionLogs(source, target, amountToDebit, amountToCredit);
    }

    private Account findAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Account not found: " + iban));
    }

    private void validateTransfer(Account source, TransferRequest request) {
        if (!source.getOwner().getEmail().equals(request.getOwnerEmail())) {
            throw new RuntimeException("Unauthorized: Access denied for this account.");
        }
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than zero.");
        }
        if (source.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient funds!");
        }
    }

    private double calculateCreditAmount(Account source, Account target, double amount) {
        if (source.getCurrency().equals(target.getCurrency())) {
            return amount;
        }
        double rate = currencyService.getExchangeRate(source.getCurrency(), target.getCurrency());
        return amount * rate;
    }

    private void updateBalances(Account source, Account target, double debit, double credit) {
        source.setBalance(source.getBalance() - debit);
        target.setBalance(target.getBalance() + credit);

        accountRepository.save(source);
        accountRepository.save(target);
    }

    private void createTransactionLogs(Account source, Account target, double debit, double credit) {
        transactionRepository.save(buildTransaction(
                source, source.getIban(), target.getIban(), -debit, source.getCurrency(), "Transfer to " + target.getIban()
        ));

        transactionRepository.save(buildTransaction(
                target, source.getIban(), target.getIban(), credit, target.getCurrency(), "Transfer from " + source.getIban()
        ));
    }

    private Transaction buildTransaction(Account account, String src, String dst, double amt, String curr, String desc) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setSourceIban(src);
        tx.setTargetIban(dst);
        tx.setAmount(amt);
        tx.setCurrency(curr);
        tx.setDescription(desc);
        tx.setTimestamp(LocalDateTime.now());
        return tx;
    }

    public double getTotalBalanceInCurrency(String email, String targetCurrency) {
        List<Account> accounts = accountRepository.findByOwnerEmail(email);

        return accounts.stream()
                .mapToDouble(acc -> {
                    if (acc.getCurrency().equals(targetCurrency)) {
                        return acc.getBalance();
                    }
                    // Call our existing conversion logic
                    double rate = currencyService.getExchangeRate(acc.getCurrency(), targetCurrency);
                    return acc.getBalance() * rate;
                })
                .sum();
    }

    public List<Transaction> getAllTransactionsForUser(String email) {
        List<Account> userAccounts = accountRepository.findByOwnerEmail(email);

        if (userAccounts.isEmpty()) {
            return List.of();
        }

        return transactionRepository.findByAccountInOrderByTimestampDesc(userAccounts);
    }
}
