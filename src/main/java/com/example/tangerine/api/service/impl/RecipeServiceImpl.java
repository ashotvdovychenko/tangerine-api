package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.service.RecipeService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
  public Optional<Recipe> findById(Long recipeId) {
    return recipeRepository.findById(recipeId);
  }

  @Override
  public void deleteById(Long id) {
    recipeRepository.deleteById(id);
  }
}
