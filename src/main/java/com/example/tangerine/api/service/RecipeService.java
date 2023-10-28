package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Recipe;
import java.util.Optional;

public interface RecipeService {
  Recipe create(Recipe recipe);

  Recipe update(Recipe recipe);

  Optional<Recipe> findById(Long recipeId);

  void deleteById(Long id);
}
