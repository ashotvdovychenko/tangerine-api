package com.example.tangerine.api.web.dto.menu;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuUpdateDto {
  @NotBlank(message = "Specify name")
  private String name;
}
