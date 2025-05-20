package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@Schema(description = "Информация о карте")
public class CardBriefInformation implements Serializable {

    public  CardBriefInformation(Card card) {
        this.cardNumber = card.getCardNumber();
        this.expirationDate = card.getExpirationDate();
        this.cardStatus = card.getCardStatus();
    }

    private String cardNumber;
    private LocalDate expirationDate;
    private CardStatus cardStatus;


}