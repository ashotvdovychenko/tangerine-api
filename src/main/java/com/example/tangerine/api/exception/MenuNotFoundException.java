package com.example.tangerine.api.exception;

public class MenuNotFoundException extends RuntimeException {
  public MenuNotFoundException(String message) {
    super(message);
  }
}
