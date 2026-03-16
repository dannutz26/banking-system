package com.dan.banking.backend.service;


import com.dan.banking.backend.dto.AccountRequest;
import com.dan.banking.backend.dto.TransferRequest;
import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.AuditLog;
import com.dan.banking.backend.entity.Transaction;
import com.dan.banking.backend.entity.User;
import com.dan.banking.backend.exceptions.*;
import com.dan.banking.backend.repository.AccountRepository;
import com.dan.banking.backend.repository.AuditLogRepository;
import com.dan.banking.backend.repository.TransactionRepository;
import com.dan.banking.backend.repository.UserRepository;
import com.dan.banking.backend.utility.IbanGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final AuditLogRepository auditLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public List<Account> getAccounts(String email) {
        logger.debug("Fetching accounts for user: {}", email);
        return accountRepository.findByOwnerEmail(email);
    }

    public void insertAccount(AccountRequest accountRequest) {
        logger.info("Attempting to create account for user: {}", accountRequest.getOwnerEmail());

        try {
            User owner = userRepository.findByEmail(accountRequest.getOwnerEmail())
                    .orElseThrow(() -> new UserNotFoundException(accountRequest.getOwnerEmail()));

            String uniqueIban;
            do {
                String country = (owner.getCountryCode() != null) ? owner.getCountryCode() : "RO";
                uniqueIban = ibanGenerator.generate(country);
            } while (accountRepository.existsByIban(uniqueIban));

            Account account = new Account();
            account.setIban(uniqueIban);
            account.setCurrency(accountRequest.getCurrency().toString());
            account.setOwner(owner);
            accountRepository.save(account);

            auditLogRepository.save(new AuditLog(
                    "ACCOUNT_CREATION_SUCCESS",
                    "Created " + account.getCurrency() + " account with IBAN: " + uniqueIban,
                    "SUCCESS"
            ));
            logger.info("Account {} created successfully.", uniqueIban);

        } catch (Exception e) {
            auditLogRepository.save(new AuditLog(
                    "ACCOUNT_CREATION_FAILURE",
                    "Failed for email: " + accountRequest.getOwnerEmail() + " Error: " + e.getMessage(),
                    "FAILURE"
            ));
            logger.error("Account creation failed: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void transferMoney(TransferRequest request) {
        String logDetails = String.format("From: %s to %s | Amount: %.2f",
                request.getFromIban(), request.getToIban(), request.getAmount());
        logger.info("Transfer attempt: {}", logDetails);

        try {
            Account source = findAccountByIban(request.getFromIban());
            Account target = findAccountByIban(request.getToIban());

            validateTransfer(source, request);

            double amountToDebit = request.getAmount();
            double amountToCredit = calculateCreditAmount(source, target, amountToDebit);

            updateBalances(source, target, amountToDebit, amountToCredit);
            createTransactionLogs(source, target, amountToDebit, amountToCredit);

            auditLogRepository.save(new AuditLog("TRANSFER_SUCCESS", logDetails, "SUCCESS"));
            logger.info("Transfer successful for {}", request.getOwnerEmail());

        } catch (Exception e) {
            logger.error("Transfer failed: {} - Details: {}", e.getMessage(), logDetails);
            auditLogRepository.save(new AuditLog("TRANSFER_FAILED", logDetails + " | Error: " + e.getMessage(), "FAILURE"));
            throw e;
        }
    }

    private Account findAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException(iban));
    }

    private void validateTransfer(Account source, TransferRequest request) {
        if (!source.getOwner().getEmail().equals(request.getOwnerEmail())) {
            String warning = String.format("SECURITY ALERT: User %s tried to transfer from account owned by %s",
                    request.getOwnerEmail(), source.getOwner().getEmail());
            logger.error(warning);
            auditLogRepository.save(new AuditLog("UNAUTHORIZED_TRANSFER_ATTEMPT", warning, "CRITICAL"));
            throw new UnauthorizedAccountAccessException(request.getOwnerEmail());
        }

        if (request.getAmount() <= 0) {
            throw new NegativeAmountException();
        }

        if (source.getBalance() < request.getAmount()) {
            logger.warn("Transfer rejected: Insufficient funds for IBAN {}", source.getIban());
            throw new InsuficientFundsException();
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
