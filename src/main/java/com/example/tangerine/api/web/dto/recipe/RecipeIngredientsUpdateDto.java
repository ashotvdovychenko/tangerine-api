package com.example.tangerine.api.web.dto.recipe;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class RecipeIngredientsUpdateDto {
  @NotNull
  private List<Long> ingredientIndices;
}
