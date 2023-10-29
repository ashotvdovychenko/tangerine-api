package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.service.RecipeService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

  private final RecipeRepository recipeRepository;

  @Override
  public Recipe create(Recipe recipe) {
    return recipeRepository.save(recipe);
  }

  @Override
  public Recipe update(Recipe recipe) {
    return recipeRepository.save(recipe);
  }

  @Override
  public List<Recipe> findAll() {
    return recipeRepository.findAll();
  }

  @Override
  public Optional<Recipe> findById(Long recipeId) {
    return recipeRepository.findById(recipeId);
  }

  @Override
  @Transactional
  public Optional<Set<Menu>> getMenus(Long recipeId) {
    return recipeRepository.findById(recipeId).map(Recipe::getMenus).map(Set::copyOf);
  }

  @Override
  public void deleteById(Long id) {
    recipeRepository.deleteById(id);
  }
}
