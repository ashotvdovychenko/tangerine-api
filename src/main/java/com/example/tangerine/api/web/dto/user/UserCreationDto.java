package com.example.tangerine.api.web.dto.user;

import lombok.Data;

@Data
public class UserCreationDto {
  private String username;
  private String email;
  private String password;
  private String phoneNumber;
}