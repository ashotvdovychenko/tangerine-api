package com.example.tangerine.api.web.dto.menu;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class MenuRecipesUpdateDto {
  @NotNull
  private List<Long> recipesIndices;
}
