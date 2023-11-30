package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Comment;
import com.example.tangerine.api.domain.Ingredient;
import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface RecipeService {
  Recipe create(Recipe recipe, List<Long> ingredientIndices, String username);

  Recipe update(Recipe recipe, List<Long> ingredientIndices);

  List<Recipe> findAll();

  Optional<Recipe> findById(Long recipeId);

  Optional<Set<Menu>> getMenus(Long recipeId);

  Optional<List<Comment>> getComments(Long recipeId);

  void deleteById(Long recipeId);

  Optional<Set<Ingredient>> getIngredients(Long recipeId);

  String addImage(Long recipeId, MultipartFile file);

  Resource getImage(Long recipeId);

  void deleteImage(Long recipeId);
}
