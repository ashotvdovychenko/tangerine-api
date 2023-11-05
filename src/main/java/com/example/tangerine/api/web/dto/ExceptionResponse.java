package com.example.tangerine.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponse {
  private String message;
  private String timeStamp;
}