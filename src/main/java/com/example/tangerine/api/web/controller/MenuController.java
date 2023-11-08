package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.MenuService;
import com.example.tangerine.api.web.dto.ExceptionResponse;
import com.example.tangerine.api.web.dto.menu.MenuCreationDto;
import com.example.tangerine.api.web.dto.menu.MenuDto;
import com.example.tangerine.api.web.dto.menu.MenuRecipesUpdateDto;
import com.example.tangerine.api.web.dto.menu.MenuUpdateDto;
import com.example.tangerine.api.web.dto.recipe.RecipeDto;
import com.example.tangerine.api.web.mapper.MenuMapper;
import com.example.tangerine.api.web.mapper.RecipeMapper;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Menu Controller")
@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;
  private final MenuMapper menuMapper;
  private final RecipeMapper recipeMapper;

  @GetMapping
  @Operation(summary = "Get all menus", responses = @ApiResponse(responseCode = "200",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = MenuDto.class)))))
  public ResponseEntity<List<MenuDto>> findAll() {
    return menuService.findAll().stream()
        .map(menuMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get menu by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MenuDto.class))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<MenuDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(menuService.findById(id).map(menuMapper::toPayload));
  }

  @GetMapping("/{id}/recipes")
  @Operation(summary = "Get recipes of menu", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = RecipeDto.class)))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<List<RecipeDto>> getRecipes(@PathVariable Long id) {
    return ResponseEntity.of(menuService.getRecipes(id)
        .map(recipes -> recipes.stream()
            .map(recipeMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/image")
  @Operation(summary = "Get image of menu", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Resource> getImage(@PathVariable Long id) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(menuService.getImage(id));
  }

  @PostMapping
  @Operation(summary = "Create new menu", responses = {
      @ApiResponse(responseCode = "201",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MenuDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<MenuDto> create(@RequestBody @Valid MenuCreationDto menuDto,
                                        Principal principal) {
    var created = menuService.create(menuMapper.toEntity(menuDto),
        menuDto.getRecipeIndices(), principal.getName());
    return new ResponseEntity<>(menuMapper.toPayload(created), HttpStatus.CREATED);
  }

  @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("@menuChecker.check(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Add image of menu by id", responses = {
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
    var imageKey = menuService.addImage(id, file);
    return new ResponseEntity<>(imageKey, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("@menuChecker.check(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Update menu by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MenuDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<MenuDto> update(@RequestBody @Valid MenuUpdateDto menuDto,
                                        @PathVariable Long id, Principal principal) {
    return ResponseEntity.of(menuService.findById(id)
        .map(menu -> menuMapper.partialUpdate(menuDto, menu))
        .map(menuService::update)
        .map(menuMapper::toPayload));
  }

  @PutMapping("/{id}/recipes")
  @PreAuthorize("@menuChecker.check(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Add recipes to menu", responses = {
      @ApiResponse(responseCode = "200", content = @Content),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Void> addRecipes(@RequestBody @Valid MenuRecipesUpdateDto menuDto,
                                         @PathVariable Long id, Principal principal) {
    menuService.addRecipes(id, menuDto.getRecipesIndices());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@menuChecker.check(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete menu by id", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content)
  })
  public ResponseEntity<Void> deleteById(@PathVariable Long id, Principal principal) {
    menuService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/recipes")
  @PreAuthorize("@menuChecker.check(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete recipes from menu", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content)
  })
  public ResponseEntity<Void> deleteRecipes(@RequestBody @Valid MenuRecipesUpdateDto menuDto,
                                            @PathVariable Long id, Principal principal) {
    menuService.deleteRecipes(id, menuDto.getRecipesIndices());
    return ResponseEntity.noContent().build();
  }


  @DeleteMapping("/{id}/image")
  @PreAuthorize("@menuChecker.check(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete image of menu by id", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Void> deleteImage(@PathVariable Long id, Principal principal) {
    menuService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }
}
