package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.service.RecipeService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

  private final RecipeRepository recipeRepository;
  private final AwsS3StorageService storageService;
  @Value("${aws.bucket}")
  private String bucket;

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

  @Override
  @Transactional
  public String addPicture(Long recipeId, MultipartFile file) {
    if (!recipeRepository.existsById(recipeId)) {
      throw new IllegalArgumentException();
    }
    var pictureKey = UUID.randomUUID().toString();
    try {
      storageService.uploadPicture(
          file.getBytes(),
          "recipe-images/%s/%s".formatted(recipeId, pictureKey),
          bucket);
    } catch (IOException e) {
      throw new RuntimeException();
    }
    recipeRepository.updatePictureUrlById(recipeId, pictureKey);
    return pictureKey;
  }

  @Override
  public Resource getPicture(Long recipeId) {
    var recipe = recipeRepository.findById(recipeId).orElseThrow(IllegalArgumentException::new);
    if (recipe.getPictureUrl() == null || recipe.getPictureUrl().isBlank()) {
      throw new IllegalArgumentException();
    }
    return storageService.findByKey(
        "recipe-images/%s/%s".formatted(recipeId, recipe.getPictureUrl()),
        bucket);
  }

  @Override
  @Transactional
  public void deletePicture(Long recipeId) {
    var recipe = recipeRepository.findById(recipeId).orElseThrow(IllegalArgumentException::new);
    if (recipe.getPictureUrl() != null) {
      storageService.deleteByKey(
          "recipe-images/%s/%s".formatted(recipeId, recipe.getPictureUrl()),
          bucket
      );
      recipe.setPictureUrl(null);
    }
  }
}
