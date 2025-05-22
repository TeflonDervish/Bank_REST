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
        String startCardNumber = "1234 1234 1234 %04d";
        String cardNumber = String.format(startCardNumber, random.nextInt(10000));
        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = String.format(startCardNumber, random.nextInt(10000));
        }
        return cardNumber;
    }
}
