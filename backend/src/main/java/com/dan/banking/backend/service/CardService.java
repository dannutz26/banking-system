package com.dan.banking.backend.service;

import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.AuditLog;
import com.dan.banking.backend.entity.Card;
import com.dan.banking.backend.repository.AccountRepository;
import com.dan.banking.backend.repository.AuditLogRepository;
import com.dan.banking.backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;
    private final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Transactional
    public Card createCard(String iban, String type) {
        logger.info("Attempting to issue {} card for IBAN: {}", type, iban);

        try {
            Account account = accountRepository.findByIban(iban)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (cardRepository.existsByAccountAndIsBlockedFalse(account)) {
                String errorMsg = "Card issuance blocked: Account " + iban + " already has an active card.";
                logger.warn(errorMsg);
                auditLogRepository.save(new AuditLog("CARD_ISSUANCE_REJECTED", errorMsg, "WARNING"));
                throw new RuntimeException("This account already has an active card.");
            }

            Card card = new Card();
            card.setAccount(account);
            card.setCardHolderName(account.getOwner().getFirstName().toUpperCase() + " " + account.getOwner().getLastName().toUpperCase());
            card.setCardNumber(generateVisaNumber());
            card.setCvv(String.format("%03d", random.nextInt(1000)));
            card.setExpiryDate(LocalDate.now().plusYears(4));
            card.setCardType(type.toUpperCase());
            card.setBlocked(false);

            Card savedCard = cardRepository.save(card);

            auditLogRepository.save(new AuditLog(
                    "CARD_ISSUANCE_SUCCESS",
                    "Issued " + type + " card ending in " + savedCard.getCardNumber().substring(12),
                    "SUCCESS"
            ));
            logger.info("Card successfully issued for IBAN: {}", iban);

            return savedCard;

        } catch (Exception e) {
            auditLogRepository.save(new AuditLog(
                    "CARD_ISSUANCE_FAILURE",
                    "Failed for IBAN: " + iban + " Error: " + e.getMessage(),
                    "FAILURE"
            ));
            logger.error("Card creation failed: {}", e.getMessage());
            throw e;
        }
    }

    public List<Card> getCardsByEmail(String email) {
        logger.debug("Fetching all cards for user: {}", email);
        return cardRepository.findByAccountOwnerEmail(email);
    }

    private String generateVisaNumber() {
        StringBuilder sb = new StringBuilder("4");
        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}