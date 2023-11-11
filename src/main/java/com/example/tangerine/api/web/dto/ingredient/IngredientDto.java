package com.example.tangerine.api.web.dto.ingredient;

import lombok.Data;

@Data
public class IngredientDto {
  private Long id;
  private String name;
  private Long createdAt;
  private String imageKey;
}
