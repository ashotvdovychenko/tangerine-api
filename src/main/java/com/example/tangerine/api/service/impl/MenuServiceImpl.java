package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.repository.MenuRepository;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.service.MenuService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

  private final MenuRepository menuRepository;
  private final RecipeRepository recipeRepository;

  @Override
  @Transactional
  public Menu create(Menu menu, List<Long> recipeIndices) {
    StreamEx.of(recipeIndices)
        .mapPartial(recipeRepository::findById)
        .forEach(menu::addRecipe);
    return menuRepository.save(menu);
  }

  @Override
  public Menu update(Menu menu) {
    return menuRepository.save(menu);
  }

  @Override
  public List<Menu> findAll() {
    return menuRepository.findAll();
  }

  @Override
  public Optional<Menu> findById(Long menuId) {
    return menuRepository.findById(menuId);
  }

  @Override
  public void deleteById(Long menuId) {
    menuRepository.deleteById(menuId);
  }

  @Override
  @Transactional
  public void addRecipes(Long menuId, List<Long> recipeIndices) {
    var menu = menuRepository.findById(menuId).orElseThrow(IllegalArgumentException::new);
    StreamEx.of(recipeIndices)
        .mapPartial(recipeRepository::findById)
        .forEach(menu::addRecipe);
  }

  @Override
  @Transactional
  public Optional<Set<Recipe>> getRecipes(Long menuId) {
    return menuRepository.findById(menuId).map(Menu::getRecipes).map(Set::copyOf);
  }

  @Override
  @Transactional
  public void deleteRecipes(Long menuId, List<Long> recipeIndices) {
    menuRepository.findById(menuId)
        .ifPresent(menu -> menu.removeRecipes(menu.getRecipes().stream()
            .filter(recipe -> recipeIndices.contains(recipe.getId()))
            .toList()));
  }
}
