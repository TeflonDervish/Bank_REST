package com.example.bankcards.exception;

public class BlockedRequestException extends RuntimeException {
  public BlockedRequestException(String message) {
    super(message);
  }
}
