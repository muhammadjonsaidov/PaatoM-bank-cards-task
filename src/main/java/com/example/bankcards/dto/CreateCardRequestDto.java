package com.example.bankcards.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCardRequestDto {
    private String cardNumber;
    private String ownerName;
    private LocalDate expiryDate;
    private BigDecimal initialBalance;
    private Long userId;
}
