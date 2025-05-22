package com.example.bankcards.exception;

/**
 * Класс для описания ошибок при работе с картами
 */
public class CardException extends RuntimeException {
  public CardException(String message) {
    super(message);
  }
}
