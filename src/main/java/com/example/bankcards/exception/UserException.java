package com.example.bankcards.exception;

/**
 * Класс для ошибок при работе с пользователями
 */
public class UserException extends RuntimeException {
  public UserException(String message) {
    super(message);
  }
}
