package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface MenuService {
  Menu create(Menu menu, List<Long> recipeIndices, String username);

  Menu update(Menu menu);

  List<Menu> findAll();

  Optional<Menu> findById(Long menuId);

  void deleteById(Long menuId);

  void addRecipes(Long menuId, List<Long> recipeIndices);

  Optional<Set<Recipe>> getRecipes(Long menuId);

  void deleteRecipes(Long menuId, List<Long> recipeIndices);

  String addPicture(Long menuId, MultipartFile file);

  Resource getPicture(Long menuId);

  void deletePicture(Long menuId);
}
