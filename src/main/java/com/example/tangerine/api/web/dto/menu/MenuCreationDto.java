package com.example.tangerine.api.web.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class MenuCreationDto {
  @NotBlank(message = "Specify name")
  private String name;

  @NotNull
  private List<Long> recipeIndices;
}
