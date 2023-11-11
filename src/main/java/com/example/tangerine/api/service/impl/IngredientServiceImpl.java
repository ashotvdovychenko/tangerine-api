package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Ingredient;
import com.example.tangerine.api.exception.ImageNotFoundException;
import com.example.tangerine.api.exception.ImageUploadException;
import com.example.tangerine.api.exception.IngredientNotFoundException;
import com.example.tangerine.api.repository.IngredientRepository;
import com.example.tangerine.api.service.IngredientService;
import com.example.tangerine.api.service.StorageService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

  private final IngredientRepository ingredientRepository;
  private final StorageService storageService;
  @Value("${aws.bucket}")
  private String bucket;

  @Override
  public Ingredient create(Ingredient ingredient) {
    return ingredientRepository.save(ingredient);
  }

  @Override
  public Ingredient update(Ingredient ingredient) {
    return ingredientRepository.save(ingredient);
  }

  @Override
  public List<Ingredient> findAll() {
    return ingredientRepository.findAll();
  }

  @Override
  public Optional<Ingredient> findById(Long ingredientId) {
    return ingredientRepository.findById(ingredientId);
  }

  @Override
  @Transactional
  public void deleteById(Long ingredientId) {
    ingredientRepository.findById(ingredientId)
        .ifPresent(ingredient -> ingredient.getRecipes()
            .forEach(recipe -> recipe.removeIngredient(ingredient)));
    ingredientRepository.deleteById(ingredientId);
  }

  @Override
  @Transactional
  public String addImage(Long ingredientId, MultipartFile file) {
    if (!ingredientRepository.existsById(ingredientId)) {
      throw new IngredientNotFoundException(
          "Ingredient with id %s not found".formatted(ingredientId));
    }
    var imageKey = UUID.randomUUID().toString();
    try {
      storageService.uploadImage(
          file.getBytes(),
          "ingredient-images/%s/%s".formatted(ingredientId, imageKey),
          bucket);
    } catch (IOException e) {
      var fileName = file.getOriginalFilename();
      throw new ImageUploadException("Failed to upload image %s".formatted(fileName));
    }
    ingredientRepository.updateImageKeyById(ingredientId, imageKey);
    return imageKey;
  }

  @Override
  public Resource getImage(Long ingredientId) {
    var ingredient = ingredientRepository.findById(ingredientId).orElseThrow(
        () -> new IngredientNotFoundException(
            "Ingredient with id %s not found".formatted(ingredientId))
    );
    if (ingredient.getImageKey() == null || ingredient.getImageKey().isBlank()) {
      throw new ImageNotFoundException(
          "Image of ingredient with id %s not found".formatted(ingredientId));
    }
    return storageService.findByKey(
        "ingredient-images/%s/%s".formatted(ingredientId, ingredient.getImageKey()),
        bucket);
  }

  @Override
  @Transactional
  public void deleteImage(Long ingredientId) {
    var ingredient = ingredientRepository.findById(ingredientId).orElseThrow(
        () -> new IngredientNotFoundException(
            "Ingredient with id %s not found".formatted(ingredientId))
    );
    if (ingredient.getImageKey() != null) {
      storageService.deleteByKey(
          "ingredient-images/%s/%s".formatted(ingredientId, ingredient.getImageKey()),
          bucket
      );
      ingredient.setImageKey(null);
    }
  }
}
