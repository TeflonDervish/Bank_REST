package com.example.bankcards.util;

import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
@RequiredArgsConstructor
public class CardNumberGenerate {

    private final CardRepository cardRepository;
    private final Random random = new Random();

    public String generateCardNumber() {
        String startCardNumber = "1234 1234 1234 ";
        String cardNumber = startCardNumber + String.valueOf(random.nextInt());
        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = startCardNumber + String.valueOf(random.nextInt());
        }
        return cardNumber;
    }
}
