package com.example.tangerine.api.web.dto.recipe;

import com.example.tangerine.api.domain.Complexity;
import lombok.Data;

@Data
public class RecipeDto {
  private Long id;
  private String name;
  private String description;
  private Long secondsDuration;
  private Long productsCost;
  private String createdAt;
  private Complexity complexity;
  private String pictureUrl;
  private Long authorId;
}
