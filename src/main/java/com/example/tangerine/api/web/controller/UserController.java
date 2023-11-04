package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.UserService;
import com.example.tangerine.api.web.dto.menu.MenuDto;
import com.example.tangerine.api.web.dto.recipe.RecipeDto;
import com.example.tangerine.api.web.dto.user.UserDto;
import com.example.tangerine.api.web.dto.user.UserUpdateDto;
import com.example.tangerine.api.web.mapper.MenuMapper;
import com.example.tangerine.api.web.mapper.RecipeMapper;
import com.example.tangerine.api.web.mapper.UserMapper;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;
  private final RecipeMapper recipeMapper;
  private final MenuMapper menuMapper;

  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return userService.findAll().stream()
        .map(userMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(userService.findById(id).map(userMapper::toPayload));
  }

  @GetMapping("/{id}/recipes")
  public ResponseEntity<List<RecipeDto>> getRecipes(@PathVariable Long id) {
    return ResponseEntity.of(userService.getRecipes(id)
        .map(recipes -> recipes.stream()
            .map(recipeMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/menus")
  public ResponseEntity<List<MenuDto>> getMenus(@PathVariable Long id) {
    return ResponseEntity.of(userService.getMenus(id)
        .map(recipes -> recipes.stream()
            .map(menuMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/picture")
  public ResponseEntity<Resource> getPicture(@PathVariable Long id) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(userService.getPicture(id));
  }

  @PostMapping("/{id}/picture")
  @PreAuthorize("@userChecker.check(#id, #principal.getName())")
  public ResponseEntity<String> uploadPicture(@PathVariable Long id,
                                              @RequestParam MultipartFile file,
                                              Principal principal) {
    var pictureKey = userService.addPicture(id, file);
    return new ResponseEntity<>(pictureKey, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("@userChecker.check(#id, #principal.getName())")
  public ResponseEntity<UserDto> update(@RequestBody UserUpdateDto userDto,
                                        @PathVariable Long id, Principal principal) {
    return ResponseEntity.of(userService.findById(id)
        .map(user -> userMapper.partialUpdate(userDto, user))
        .map(userService::update)
        .map(userMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@userChecker.check(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteById(@PathVariable Long id, Principal principal) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/picture")
  @PreAuthorize("@userChecker.check(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> deletePicture(@PathVariable Long id, Principal principal) {
    userService.deletePicture(id);
    return ResponseEntity.noContent().build();
  }
}
