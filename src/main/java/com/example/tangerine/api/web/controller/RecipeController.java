package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.RecipeService;
import com.example.tangerine.api.web.dto.menu.MenuDto;
import com.example.tangerine.api.web.dto.recipe.RecipeCreationDto;
import com.example.tangerine.api.web.dto.recipe.RecipeDto;
import com.example.tangerine.api.web.dto.recipe.RecipeUpdateDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

  private final RecipeService recipeService;
  private final RecipeMapper recipeMapper;
  private final MenuMapper menuMapper;

  @GetMapping
  public ResponseEntity<List<RecipeDto>> findAll() {
    return recipeService.findAll().stream()
        .map(recipeMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RecipeDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(recipeService.findById(id).map(recipeMapper::toPayload));
  }

  @GetMapping("/{id}/menus")
  public ResponseEntity<List<MenuDto>> getMenus(@PathVariable Long id) {
    return ResponseEntity.of(recipeService.getMenus(id)
        .map(menus -> menus.stream()
            .map(menuMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/picture")
  public ResponseEntity<Resource> getPicture(@PathVariable Long id) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(recipeService.getPicture(id));
  }

  @PostMapping
  public ResponseEntity<RecipeDto> create(@RequestBody RecipeCreationDto recipeDto) {
    var created = recipeService.create(recipeMapper.toEntity(recipeDto));
    return new ResponseEntity<>(recipeMapper.toPayload(created), HttpStatus.CREATED);
  }

  @PostMapping("/{id}/picture")
  public ResponseEntity<String> uploadPicture(@PathVariable Long id,
                                              @RequestParam MultipartFile file) {
    var pictureKey = recipeService.addPicture(id, file);
    return new ResponseEntity<>(pictureKey, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<RecipeDto> update(@RequestBody RecipeUpdateDto recipeDto,
                                          @PathVariable Long id) {
    return ResponseEntity.of(recipeService.findById(id)
        .map(menu -> recipeMapper.partialUpdate(recipeDto, menu))
        .map(recipeService::update)
        .map(recipeMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    recipeService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/picture")
  public ResponseEntity<Void> deletePicture(@PathVariable Long id) {
    recipeService.deletePicture(id);
    return ResponseEntity.noContent().build();
  }
}