package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MenuService {
  Menu create(Menu menu, List<Long> recipeIndices);

  Menu update(Menu menu);

  List<Menu> findAll();

  Optional<Menu> findById(Long menuId);

  void deleteById(Long menuId);

  void addRecipes(Long menuId, List<Long> recipeIndices);

  Optional<Set<Recipe>> getRecipes(Long menuId);

  void deleteRecipes(Long menuId, List<Long> recipeIndices);
}
