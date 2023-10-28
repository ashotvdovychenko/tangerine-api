package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.repository.MenuRepository;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.service.MenuService;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

  private final MenuRepository menuRepository;
  private final RecipeRepository recipeRepository;

  @Override
  public Menu create(Menu menu) {
    return menuRepository.save(menu);
  }

  @Override
  public Menu update(Menu menu) {
    return menuRepository.save(menu);
  }

  @Override
  public Optional<Menu> findById(Long menuId) {
    return menuRepository.findById(menuId);
  }

  @Override
  @Transactional
  public void addRecipe(Long menuId, Long recipeId) {
    var menu = menuRepository.findById(menuId).orElseThrow(IllegalArgumentException::new);
    var recipe = recipeRepository.findById(recipeId).orElseThrow(IllegalArgumentException::new);
    menu.addRecipe(recipe);
  }

  @Override
  public Optional<Set<Recipe>> getRecipes(Long menuId) {
    return menuRepository.findById(menuId).map(Menu::getRecipes).map(Set::copyOf);
  }

  @Override
  public Optional<Integer> getRecipesCount(Long menuId) {
    return menuRepository.findById(menuId).map(menu -> menu.getRecipes().size());
  }

  @Override
  @Transactional
  public void deleteRecipe(Long menuId, Long recipeId) {
    menuRepository.findById(menuId)
        .ifPresent(menu -> recipeRepository.findById(recipeId)
            .ifPresent(menu::removeRecipe));
  }
}
