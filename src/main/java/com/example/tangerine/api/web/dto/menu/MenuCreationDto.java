package com.example.tangerine.api.web.dto.menu;

import java.util.List;
import lombok.Data;

@Data
public class MenuCreationDto {
  private String name;
  private List<Long> recipeIndices;
}
