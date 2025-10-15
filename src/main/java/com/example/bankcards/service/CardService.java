package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequestDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    // ----- USER -----

    public Page<CardDto> getUserCards(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return cardRepository.findByUserId(user.getId(), pageable).map(this::convertToDto);
    }

    @Transactional
    public void transferMoney(TransferRequestDto request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new EntityNotFoundException("Source card not found"));
        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new EntityNotFoundException("Destination card not found"));

        if (!fromCard.getUser().getId().equals(user.getId()) || !toCard.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User can only transfer between their own cards");
        }
        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Both cards must be active for transfer");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    // ----- ADMIN -----

    public Card createCard(CreateCardRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found for card creation"));

        Card card = new Card();
        card.setCardNumber(request.getCardNumber());
        card.setOwnerName(request.getOwnerName());
        card.setExpiryDate(request.getExpiryDate());
        card.setBalance(request.getInitialBalance());
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);

        return cardRepository.save(card);
    }

    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::convertToDto);
    }
    
    public Card updateCardStatus(Long cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
        card.setStatus(status);
        return cardRepository.save(card);
    }


    // ----- CONVERT -----
    private CardDto convertToDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setMaskedCardNumber(CardUtil.maskCardNumber(card.getCardNumber()));
        dto.setOwnerName(card.getOwnerName());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus().name());
        dto.setBalance(card.getBalance());
        return dto;
    }
}