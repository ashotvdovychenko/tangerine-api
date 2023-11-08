package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.UserService;
import com.example.tangerine.api.web.dto.ExceptionResponse;
import com.example.tangerine.api.web.dto.menu.MenuDto;
import com.example.tangerine.api.web.dto.recipe.RecipeDto;
import com.example.tangerine.api.web.dto.user.UserDto;
import com.example.tangerine.api.web.dto.user.UserUpdateDto;
import com.example.tangerine.api.web.mapper.MenuMapper;
import com.example.tangerine.api.web.mapper.RecipeMapper;
import com.example.tangerine.api.web.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User Controller")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;
  private final RecipeMapper recipeMapper;
  private final MenuMapper menuMapper;

  @GetMapping
  @Operation(summary = "Get all users", responses = @ApiResponse(responseCode = "200",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))))
  public ResponseEntity<List<UserDto>> findAll() {
    return userService.findAll().stream()
        .map(userMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(userService.findById(id).map(userMapper::toPayload));
  }

  @GetMapping("/{id}/recipes")
  @Operation(summary = "Get recipes of user", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = RecipeDto.class)))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<List<RecipeDto>> getRecipes(@PathVariable Long id) {
    return ResponseEntity.of(userService.getRecipes(id)
        .map(recipes -> recipes.stream()
            .map(recipeMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/menus")
  @Operation(summary = "Get menus of user", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = MenuDto.class)))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<List<MenuDto>> getMenus(@PathVariable Long id) {
    return ResponseEntity.of(userService.getMenus(id)
        .map(recipes -> recipes.stream()
            .map(menuMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/image")
  @Operation(summary = "Get image of user", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Resource> getImage(@PathVariable Long id) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(userService.getImage(id));
  }

  @PostMapping("/{id}/image")
  @PreAuthorize("@userChecker.check(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Add image to user", responses = {
      @ApiResponse(responseCode = "201",
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
              schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestPart MultipartFile file,
                                              Principal principal) {
    var imageKey = userService.addImage(id, file);
    return new ResponseEntity<>(imageKey, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("@userChecker.check(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Update user by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> update(@RequestBody @Valid UserUpdateDto userDto,
                                        @PathVariable Long id, Principal principal) {
    return ResponseEntity.of(userService.findById(id)
        .map(user -> userMapper.partialUpdate(userDto, user))
        .map(userService::update)
        .map(userMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@userChecker.check(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete user by id", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
  })
  public ResponseEntity<Void> deleteById(@PathVariable Long id, Principal principal) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/image")
  @PreAuthorize("@userChecker.check(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete image of user", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Void> deleteImage(@PathVariable Long id, Principal principal) {
    userService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }
}
