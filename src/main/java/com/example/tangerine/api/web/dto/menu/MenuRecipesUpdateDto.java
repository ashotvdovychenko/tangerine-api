package com.example.tangerine.api.web.dto.menu;

import java.util.List;
import lombok.Data;

@Data
public class MenuRecipesUpdateDto {
  private List<Long> recipesIndices;
}
