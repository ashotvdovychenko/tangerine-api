package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Comment;
import com.example.tangerine.api.domain.Ingredient;
import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.exception.ImageNotFoundException;
import com.example.tangerine.api.exception.ImageUploadException;
import com.example.tangerine.api.exception.MenuNotFoundException;
import com.example.tangerine.api.exception.RecipeNotFoundException;
import com.example.tangerine.api.exception.UserNotFoundException;
import com.example.tangerine.api.repository.IngredientRepository;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.service.RecipeService;
import com.example.tangerine.api.service.StorageService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

  private final RecipeRepository recipeRepository;
  private final UserRepository userRepository;
  private final IngredientRepository ingredientRepository;
  private final StorageService storageService;
  @Value("${aws.bucket}")
  private String bucket;

  @Override
  @Transactional
  public Recipe create(Recipe recipe, List<Long> ingredientIndices, String username) {
    recipe.setAuthor(userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(
            "User with username %s not found".formatted(username))));
    StreamEx.of(ingredientIndices)
        .mapPartial(ingredientRepository::findById)
        .forEach(recipe::addIngredient);
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
  @Transactional
  public Optional<List<Comment>> getComments(Long recipeId) {
    return recipeRepository.findById(recipeId).map(Recipe::getComments).map(List::copyOf);
  }

  @Override
  @Transactional
  public void deleteById(Long recipeId) {
    recipeRepository.findById(recipeId)
        .ifPresent(recipe -> recipe.getMenus()
            .forEach(menu -> menu.removeRecipe(recipe)));
    recipeRepository.deleteById(recipeId);
  }

  @Override
  @Transactional
  public void addIngredients(Long recipeId, List<Long> ingredientIndices) {
    var recipe = recipeRepository.findById(recipeId).orElseThrow(
        () -> new MenuNotFoundException("Recipe with id %s not found".formatted(recipeId))
    );
    StreamEx.of(ingredientIndices)
        .mapPartial(ingredientRepository::findById)
        .forEach(recipe::addIngredient);
  }

  @Override
  @Transactional
  public Optional<Set<Ingredient>> getIngredients(Long recipeId) {
    return recipeRepository.findById(recipeId).map(Recipe::getIngredients).map(Set::copyOf);
  }

  @Override
  @Transactional
  public void deleteIngredients(Long recipeId, List<Long> ingredientIndices) {
    recipeRepository.findById(recipeId)
        .ifPresent(recipe -> recipe.removeIngredients(recipe.getIngredients().stream()
            .filter(ingredient -> ingredientIndices.contains(ingredient.getId()))
            .toList()));
  }

  @Override
  @Transactional
  public String addImage(Long recipeId, MultipartFile file) {
    if (!recipeRepository.existsById(recipeId)) {
      throw new RecipeNotFoundException("Recipe with id %s not found".formatted(recipeId));
    }
    var imageKey = UUID.randomUUID().toString();
    try {
      storageService.uploadImage(
          file.getBytes(),
          "recipe-images/%s/%s".formatted(recipeId, imageKey),
          bucket);
    } catch (IOException e) {
      var fileName = file.getOriginalFilename();
      throw new ImageUploadException("Failed to upload image %s".formatted(fileName));
    }
    recipeRepository.updateImageKeyById(recipeId, imageKey);
    return imageKey;
  }

  @Override
  public Resource getImage(Long recipeId) {
    var recipe = recipeRepository.findById(recipeId).orElseThrow(
        () -> new RecipeNotFoundException("Recipe with id %s not found".formatted(recipeId)));
    if (recipe.getImageKey() == null || recipe.getImageKey().isBlank()) {
      throw new ImageNotFoundException("Image of recipe with id %s not found".formatted(recipeId));
    }
    return storageService.findByKey(
        "recipe-images/%s/%s".formatted(recipeId, recipe.getImageKey()),
        bucket);
  }

  @Override
  @Transactional
  public void deleteImage(Long recipeId) {
    var recipe = recipeRepository.findById(recipeId).orElseThrow(
        () -> new RecipeNotFoundException("Recipe with id %s not found".formatted(recipeId)));
    if (recipe.getImageKey() != null) {
      storageService.deleteByKey(
          "recipe-images/%s/%s".formatted(recipeId, recipe.getImageKey()),
          bucket
      );
      recipe.setImageKey(null);
    }
  }
}
