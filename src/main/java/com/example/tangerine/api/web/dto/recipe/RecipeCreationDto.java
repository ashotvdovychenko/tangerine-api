package com.example.tangerine.api.web.dto.recipe;

import com.example.tangerine.api.domain.Complexity;
import lombok.Data;

@Data
public class RecipeCreationDto {
  private String name;
  private String description;
  private Long secondsDuration;
  private Long productsCost;
  private Complexity complexity;
}
