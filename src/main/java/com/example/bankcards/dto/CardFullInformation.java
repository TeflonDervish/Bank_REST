package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Schema(description = "Полная информация о карте")
public class CardFullInformation implements Serializable {

    public CardFullInformation(Card card) {
        this.cardNumber = card.getCardNumber();
        this.expirationDate = card.getExpirationDate();
        this.cardStatus = card.getCardStatus();
        this.user = card.getUser();
        this.balance = card.getBalance();
    }

    private String cardNumber;
    private LocalDate expirationDate;
    private CardStatus cardStatus;

    private User user;
    private BigDecimal balance;

}
