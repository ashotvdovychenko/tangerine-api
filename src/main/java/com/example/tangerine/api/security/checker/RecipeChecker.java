package com.example.tangerine.api.security.checker;

import com.example.tangerine.api.exception.RecipeNotFoundException;
import com.example.tangerine.api.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeChecker {
  private final RecipeRepository recipeRepository;

  public boolean check(Long id, String username) {
    if (id == null || username == null) {
      return false;
    }
    var recipe = recipeRepository.findById(id).orElseThrow(
        () -> new RecipeNotFoundException("Recipe with id %s not found".formatted(id))
    );
    return recipe.getAuthor().getUsername().equals(username);
  }
}
