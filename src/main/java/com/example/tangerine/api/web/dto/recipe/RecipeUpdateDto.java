package com.example.tangerine.api.web.dto.recipe;

import com.example.tangerine.api.domain.Complexity;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.Data;

@Data
public class RecipeUpdateDto {
  private String name;

  private String description;

  @PositiveOrZero(message = "Must not be negative")
  private Long secondsDuration;

  @PositiveOrZero(message = "Must not be negative")
  private Long productsCost;

  private Complexity complexity;

  private List<Long> ingredientIndices;
}
