package com.example.bankcards.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardDto {
    private Long id;
    private String maskedCardNumber;
    private String ownerName;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal balance;
}