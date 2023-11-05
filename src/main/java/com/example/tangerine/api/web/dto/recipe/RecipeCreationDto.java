package com.example.tangerine.api.web.dto.recipe;

import com.example.tangerine.api.domain.Complexity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class RecipeCreationDto {
  @NotBlank(message = "Specify name")
  private String name;

  @NotBlank(message = "Specify description")
  private String description;

  @PositiveOrZero(message = "Must not be negative")
  private Long secondsDuration;

  @PositiveOrZero(message = "Must not be negative")
  private Long productsCost;

  private Complexity complexity;
}
