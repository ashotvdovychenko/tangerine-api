package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.exception.ImageNotFoundException;
import com.example.tangerine.api.exception.ImageUploadException;
import com.example.tangerine.api.exception.MenuNotFoundException;
import com.example.tangerine.api.exception.UserNotFoundException;
import com.example.tangerine.api.repository.MenuRepository;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.service.MenuService;
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
public class MenuServiceImpl implements MenuService {

  private final MenuRepository menuRepository;
  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final StorageService storageService;
  @Value("${aws.bucket}")
  private String bucket;

  @Override
  @Transactional
  public Menu create(Menu menu, List<Long> recipeIndices, String username) {
    menu.setAuthor(userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(
            "User with username %s not found".formatted(username))));
    StreamEx.of(recipeIndices)
        .mapPartial(recipeRepository::findById)
        .forEach(menu::addRecipe);
    return menuRepository.save(menu);
  }

  @Override
  @Transactional
  public Menu update(Menu menu, List<Long> recipeIndices) {
    menu.rewriteRecipes(StreamEx.of(recipeIndices)
        .mapPartial(recipeRepository::findById)
        .toList());
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
  public Optional<Set<Recipe>> getRecipes(Long menuId) {
    return menuRepository.findById(menuId).map(Menu::getRecipes).map(Set::copyOf);
  }

  @Override
  @Transactional
  public String addImage(Long menuId, MultipartFile file) {
    if (!menuRepository.existsById(menuId)) {
      throw new MenuNotFoundException("Menu with id %s not found".formatted(menuId));
    }
    var imageKey = UUID.randomUUID().toString();
    try {
      storageService.uploadImage(
          file.getBytes(),
          "menu-images/%s/%s".formatted(menuId, imageKey),
          bucket);
    } catch (IOException e) {
      var fileName = file.getOriginalFilename();
      throw new ImageUploadException("Failed to upload image %s".formatted(fileName));
    }
    menuRepository.updateImageKeyById(menuId, imageKey);
    return imageKey;
  }

  @Override
  public Resource getImage(Long menuId) {
    var menu = menuRepository.findById(menuId).orElseThrow(
        () -> new MenuNotFoundException("Menu with id %s not found".formatted(menuId))
    );
    if (menu.getImageKey() == null || menu.getImageKey().isBlank()) {
      throw new ImageNotFoundException("Image of menu with id %s not found".formatted(menuId));
    }
    return storageService.findByKey(
        "menu-images/%s/%s".formatted(menuId, menu.getImageKey()),
        bucket);
  }

  @Override
  @Transactional
  public void deleteImage(Long menuId) {
    var menu = menuRepository.findById(menuId).orElseThrow(
        () -> new MenuNotFoundException("Menu with id %s not found".formatted(menuId))
    );
    if (menu.getImageKey() != null) {
      storageService.deleteByKey(
          "menu-images/%s/%s".formatted(menuId, menu.getImageKey()),
          bucket
      );
      menu.setImageKey(null);
    }
  }
}
