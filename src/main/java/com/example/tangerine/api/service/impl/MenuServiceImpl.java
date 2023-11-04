package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.repository.MenuRepository;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.service.MenuService;
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
  private final RecipeRepository recipeRepository;
  private final AwsS3StorageService storageService;
  @Value("${aws.bucket}")
  private String bucket;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public Menu create(Menu menu, List<Long> recipeIndices, String username) {
    menu.setAuthor(userRepository.findByUsername(username)
        .orElseThrow(IllegalArgumentException::new));
    StreamEx.of(recipeIndices)
        .mapPartial(recipeRepository::findById)
        .forEach(menu::addRecipe);
    return menuRepository.save(menu);
  }

  @Override
  public Menu update(Menu menu) {
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
  public void addRecipes(Long menuId, List<Long> recipeIndices) {
    var menu = menuRepository.findById(menuId).orElseThrow(IllegalArgumentException::new);
    StreamEx.of(recipeIndices)
        .mapPartial(recipeRepository::findById)
        .forEach(menu::addRecipe);
  }

  @Override
  @Transactional
  public Optional<Set<Recipe>> getRecipes(Long menuId) {
    return menuRepository.findById(menuId).map(Menu::getRecipes).map(Set::copyOf);
  }

  @Override
  @Transactional
  public void deleteRecipes(Long menuId, List<Long> recipeIndices) {
    menuRepository.findById(menuId)
        .ifPresent(menu -> menu.removeRecipes(menu.getRecipes().stream()
            .filter(recipe -> recipeIndices.contains(recipe.getId()))
            .toList()));
  }

  @Override
  @Transactional
  public String addPicture(Long menuId, MultipartFile file) {
    if (!menuRepository.existsById(menuId)) {
      throw new IllegalArgumentException();
    }
    var pictureKey = UUID.randomUUID().toString();
    try {
      storageService.uploadPicture(
          file.getBytes(),
          "menu-images/%s/%s".formatted(menuId, pictureKey),
          bucket);
    } catch (IOException e) {
      throw new RuntimeException();
    }
    menuRepository.updatePictureUrlById(menuId, pictureKey);
    return pictureKey;
  }

  @Override
  public Resource getPicture(Long menuId) {
    var menu = menuRepository.findById(menuId).orElseThrow(IllegalArgumentException::new);
    if (menu.getPictureUrl() == null || menu.getPictureUrl().isBlank()) {
      throw new IllegalArgumentException();
    }
    return storageService.findByKey(
        "menu-images/%s/%s".formatted(menuId, menu.getPictureUrl()),
        bucket);
  }

  @Override
  @Transactional
  public void deletePicture(Long menuId) {
    var menu = menuRepository.findById(menuId).orElseThrow(IllegalArgumentException::new);
    if (menu.getPictureUrl() != null) {
      storageService.deleteByKey(
          "menu-images/%s/%s".formatted(menuId, menu.getPictureUrl()),
          bucket
      );
      menu.setPictureUrl(null);
    }
  }
}
