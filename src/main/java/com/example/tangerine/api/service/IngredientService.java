package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Ingredient;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IngredientService {
  Ingredient create(Ingredient ingredient);

  Ingredient update(Ingredient ingredient);

  List<Ingredient> findAll();

  Optional<Ingredient> findById(Long ingredientId);

  void deleteById(Long ingredientId);

  String addImage(Long ingredientId, MultipartFile file);

  Resource getImage(Long ingredientId);

  void deleteImage(Long ingredientId);
}
