package com.dan.banking.backend.service;

import com.dan.banking.backend.entity.Account;
import com.dan.banking.backend.entity.AuditLog;
import com.dan.banking.backend.entity.Card;
import com.dan.banking.backend.repository.AccountRepository;
import com.dan.banking.backend.repository.AuditLogRepository;
import com.dan.banking.backend.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private final Random random = new Random();

    public List<Card> getCardsByEmail(String email) {
        return cardRepository.findByEmail(email);
    }

    @Transactional
    public Card createCard(String iban, String type, boolean disposable) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!disposable && cardRepository.hasActiveCard(account.getId()) > 0) {
            throw new RuntimeException("Active card already exists for this account.");
        }

        Card card = new Card();
        card.setAccountId(account.getId());
        card.setCardHolderName(account.getOwner().getFirstName().toUpperCase() + " " + account.getOwner().getLastName().toUpperCase());
        card.setCardNumber("4" + String.format("%015d", Math.abs(random.nextLong() % 1000000000000000L)));
        card.setCvv(String.format("%03d", random.nextInt(1000)));
        card.setExpiryDate(LocalDate.now().plusYears(4));
        card.setCardType(type.toUpperCase());
        card.setDisposable(disposable);
        card.setBlocked(false);

        Card savedCard = cardRepository.save(card);

        auditLogRepository.save(new AuditLog("CARD_CREATION",
                (disposable ? "Disposable " : "Permanent ") + "card " + savedCard.getCardNumber() + " created for IBAN: " + iban,
                "SUCCESS"));

        return savedCard;
    }

    @Transactional
    public void toggleBlock(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setBlocked(!card.isBlocked());
        cardRepository.save(card);

        String status = card.isBlocked() ? "FROZEN" : "UNFROZEN";
        auditLogRepository.save(new AuditLog("CARD_STATUS_CHANGE",
                "Card " + card.getCardNumber() + " status changed to " + status,
                "SUCCESS"));
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        auditLogRepository.save(new AuditLog("CARD_DELETION",
                "Card " + card.getCardNumber() + " (ID: " + cardId + ") was permanently cancelled.",
                "SUCCESS"));

        cardRepository.delete(card);
    }
}