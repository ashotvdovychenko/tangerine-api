package com.example.tangerine.api.web;

import com.example.tangerine.api.exception.ImageNotFoundException;
import com.example.tangerine.api.exception.ImageUploadException;
import com.example.tangerine.api.exception.InvalidPasswordException;
import com.example.tangerine.api.exception.MenuNotFoundException;
import com.example.tangerine.api.exception.RecipeNotFoundException;
import com.example.tangerine.api.exception.RoleNotFoundException;
import com.example.tangerine.api.exception.UserAlreadyExistsException;
import com.example.tangerine.api.exception.UserNotFoundException;
import com.example.tangerine.api.web.dto.ExceptionResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TangerineApiControllerAdvice {

  @ExceptionHandler({
      ImageUploadException.class,
      HttpMessageNotReadableException.class,
      UserAlreadyExistsException.class
  })
  public ResponseEntity<ExceptionResponse> handleBadRequest(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(exceptionResponse(exception.getMessage()));
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ExceptionResponse> handleForbidden(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(exceptionResponse(exception.getMessage()));
  }

  @ExceptionHandler({
      UserNotFoundException.class,
      RecipeNotFoundException.class,
      MenuNotFoundException.class,
      RoleNotFoundException.class,
      ImageNotFoundException.class
  })
  public ResponseEntity<ExceptionResponse> handleNotFound(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(exceptionResponse(exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, List<String>>> handleValidationError(
      MethodArgumentNotValidException exception) {
    var errors = exception.getFieldErrors().stream()
        .filter(fe -> fe.getDefaultMessage() != null)
        .collect(Collectors.groupingBy(FieldError::getField,
            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
    return ResponseEntity.badRequest().body(errors);
  }

  private ExceptionResponse exceptionResponse(String message) {
    return new ExceptionResponse(message,
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
  }
}
