package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import java.util.Optional;
import java.util.Set;

public interface MenuService {
  Menu create(Menu menu);

  Menu update(Menu menu);

  Optional<Menu> findById(Long menuId);

  void addRecipe(Long menuId, Long recipeId);

  Optional<Set<Recipe>> getRecipes(Long menuId);

  Optional<Integer> getRecipesCount(Long menuId);

  void deleteRecipe(Long menuId, Long recipeId);
}
