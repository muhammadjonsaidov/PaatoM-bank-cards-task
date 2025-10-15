package com.example.bankcards.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}