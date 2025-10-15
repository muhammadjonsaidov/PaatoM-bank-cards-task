package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequestDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody CreateCardRequestDto request) {
        return ResponseEntity.ok(cardService.createCard(request));
    }

    @GetMapping
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardService.getAllCards(pageable);
    }
    
    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.updateCardStatus(id, CardStatus.BLOCKED));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.updateCardStatus(id, CardStatus.ACTIVE));
    }
}