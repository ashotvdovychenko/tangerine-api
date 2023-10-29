package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeService {
  Recipe create(Recipe recipe);

  Recipe update(Recipe recipe);

  List<Recipe> findAll();

  Optional<Recipe> findById(Long recipeId);

  Optional<Set<Menu>> getMenus(Long recipeId);

  void deleteById(Long id);
}
