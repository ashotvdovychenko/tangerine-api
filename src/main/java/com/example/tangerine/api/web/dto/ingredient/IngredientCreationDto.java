package com.example.tangerine.api.web.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IngredientCreationDto {
  @NotBlank(message = "Specify name")
  private String name;
}
