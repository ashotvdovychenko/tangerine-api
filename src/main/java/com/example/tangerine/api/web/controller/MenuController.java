package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.MenuService;
import com.example.tangerine.api.web.dto.menu.MenuCreationDto;
import com.example.tangerine.api.web.dto.menu.MenuDto;
import com.example.tangerine.api.web.dto.menu.MenuRecipesUpdateDto;
import com.example.tangerine.api.web.dto.menu.MenuUpdateDto;
import com.example.tangerine.api.web.dto.recipe.RecipeDto;
import com.example.tangerine.api.web.mapper.MenuMapper;
import com.example.tangerine.api.web.mapper.RecipeMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;
  private final MenuMapper menuMapper;
  private final RecipeMapper recipeMapper;

  @GetMapping
  public ResponseEntity<List<MenuDto>> findAll() {
    return menuService.findAll().stream()
        .map(menuMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  public ResponseEntity<MenuDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(menuService.findById(id).map(menuMapper::toPayload));
  }

  @GetMapping("/{id}/recipes")
  public ResponseEntity<List<RecipeDto>> findRecipes(@PathVariable Long id) {
    return ResponseEntity.of(menuService.getRecipes(id)
        .map(recipes -> recipes.stream()
            .map(recipeMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/picture")
  public ResponseEntity<Resource> getPicture(@PathVariable Long id) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(menuService.getPicture(id));
  }

  @PostMapping
  public ResponseEntity<MenuDto> create(@RequestBody MenuCreationDto menuDto) {
    var created = menuService.create(menuMapper.toEntity(menuDto), menuDto.getRecipeIndices());
    return new ResponseEntity<>(menuMapper.toPayload(created), HttpStatus.CREATED);
  }

  @PostMapping("/{id}/picture")
  public ResponseEntity<String> uploadPicture(@PathVariable Long id,
                                              @RequestParam MultipartFile file) {
    var pictureKey = menuService.addPicture(id, file);
    return new ResponseEntity<>(pictureKey, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<MenuDto> update(@RequestBody MenuUpdateDto menuDto,
                                        @PathVariable Long id) {
    return ResponseEntity.of(menuService.findById(id)
        .map(menu -> menuMapper.partialUpdate(menuDto, menu))
        .map(menuService::update)
        .map(menuMapper::toPayload));
  }

  @PutMapping("/{id}/recipes")
  public ResponseEntity<Void> addRecipes(@RequestBody MenuRecipesUpdateDto menuDto,
                                         @PathVariable Long id) {
    menuService.addRecipes(id, menuDto.getRecipesIndices());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    menuService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/recipes")
  public ResponseEntity<Void> deleteRecipes(@RequestBody MenuRecipesUpdateDto menuDto,
                                            @PathVariable Long id) {
    menuService.deleteRecipes(id, menuDto.getRecipesIndices());
    return ResponseEntity.noContent().build();
  }


  @DeleteMapping("/{id}/picture")
  public ResponseEntity<Void> deletePicture(@PathVariable Long id) {
    menuService.deletePicture(id);
    return ResponseEntity.noContent().build();
  }
}