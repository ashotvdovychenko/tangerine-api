package com.example.tangerine.api.exception;

public class IngredientNotFoundException extends RuntimeException {
  public IngredientNotFoundException(String message) {
    super(message);
  }
}
