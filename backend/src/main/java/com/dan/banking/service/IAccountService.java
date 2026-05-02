package com.dan.banking.service;

import com.dan.banking.dto.AccountRequest;
import com.dan.banking.dto.TransferRequest;
import com.dan.banking.entity.Account;
import com.dan.banking.entity.Transaction;

import java.util.List;

public interface IAccountService {
    List<Account> getAccounts(String email);
    void insertAccount(AccountRequest accountRequest);
    void transferMoney(TransferRequest request);
    double getTotalBalanceInCurrency(String email, String targetCurrency);
    List<Transaction> getAllTransactionsForUser(String email);
    void depositMoney(String iban, Double amount);
}