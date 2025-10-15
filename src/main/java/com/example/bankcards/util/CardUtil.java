package com.example.bankcards.util;

public class CardUtil {
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() <= 4) {
            return cardNumber;
        }
        int visibleCount = 4;
        String lastDigits = cardNumber.substring(cardNumber.length() - visibleCount);
        return "**** **** **** " + lastDigits;
    }
}