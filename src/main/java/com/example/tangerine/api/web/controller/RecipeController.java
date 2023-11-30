package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.CommentService;
import com.example.tangerine.api.service.RecipeService;
import com.example.tangerine.api.web.dto.ExceptionResponse;
import com.example.tangerine.api.web.dto.comment.CommentCreationDto;
import com.example.tangerine.api.web.dto.comment.CommentDto;
import com.example.tangerine.api.web.dto.ingredient.IngredientDto;
import com.example.tangerine.api.web.dto.menu.MenuDto;
import com.example.tangerine.api.web.dto.recipe.RecipeCreationDto;
import com.example.tangerine.api.web.dto.recipe.RecipeDto;
import com.example.tangerine.api.web.dto.recipe.RecipeUpdateDto;
import com.example.tangerine.api.web.mapper.CommentMapper;
import com.example.tangerine.api.web.mapper.IngredientMapper;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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

@Tag(name = "Recipe Controller")
@CrossOrigin
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

  private final RecipeService recipeService;
  private final CommentService commentService;
  private final RecipeMapper recipeMapper;
  private final MenuMapper menuMapper;
  private final CommentMapper commentMapper;
  private final IngredientMapper ingredientMapper;

  @GetMapping
  @Operation(summary = "Get all recipes", responses = @ApiResponse(responseCode = "200",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = RecipeDto.class)))))
  public ResponseEntity<List<RecipeDto>> findAll() {
    return recipeService.findAll().stream()
        .map(recipeMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get recipe by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RecipeDto.class))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<RecipeDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(recipeService.findById(id).map(recipeMapper::toPayload));
  }

  @GetMapping("/{id}/menus")
  @Operation(summary = "Get menus of recipe", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = MenuDto.class)))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<List<MenuDto>> getMenus(@PathVariable Long id) {
    return ResponseEntity.of(recipeService.getMenus(id)
        .map(menus -> menus.stream()
            .map(menuMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/comments")
  @Operation(summary = "Get comments of recipe", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long id) {
    return ResponseEntity.of(recipeService.getComments(id)
        .map(menus -> menus.stream()
            .map(commentMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/ingredients")
  @Operation(summary = "Get ingredients of recipe", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class)))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<List<IngredientDto>> getIngredients(@PathVariable Long id) {
    return ResponseEntity.of(recipeService.getIngredients(id)
        .map(ingredients -> ingredients.stream()
            .map(ingredientMapper::toPayload).toList()));
  }

  @GetMapping("/{id}/image")
  @Operation(summary = "Get image of recipe", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Resource> getImage(@PathVariable Long id) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(recipeService.getImage(id));
  }

  @PostMapping
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Create new recipe", responses = {
      @ApiResponse(responseCode = "201",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RecipeDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<RecipeDto> create(@RequestBody @Valid RecipeCreationDto recipeDto,
                                          Principal principal) {
    var created = recipeService.create(recipeMapper.toEntity(recipeDto),
        recipeDto.getIngredientIndices(), principal.getName());
    return new ResponseEntity<>(recipeMapper.toPayload(created), HttpStatus.CREATED);
  }

  @PostMapping("/{id}/comments")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Add comment to recipe", responses = {
      @ApiResponse(responseCode = "201",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CommentDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<CommentDto> addComment(@PathVariable Long id,
                                               @RequestBody @Valid CommentCreationDto commentDto,
                                               Principal principal) {
    var comment = commentService.create(
        commentMapper.toEntity(commentDto), id, principal.getName());
    return new ResponseEntity<>(commentMapper.toPayload(comment), HttpStatus.CREATED);
  }

  @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("@recipeChecker.isAuthor(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Add image to recipe", responses = {
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
    var imageKey = recipeService.addImage(id, file);
    return new ResponseEntity<>(imageKey, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("@recipeChecker.isAuthor(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Update recipe by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RecipeDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<RecipeDto> update(@RequestBody @Valid RecipeUpdateDto recipeDto,
                                          @PathVariable Long id, Principal principal) {
    return ResponseEntity.of(recipeService.findById(id)
        .map(recipe -> recipeMapper.partialUpdate(recipeDto, recipe))
        .map(recipe -> recipeService.update(recipe, recipeDto.getIngredientIndices()))
        .map(recipeMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@recipeChecker.isAuthor(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete recipe by id", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content)
  })
  public ResponseEntity<Void> deleteById(@PathVariable Long id, Principal principal) {
    recipeService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/image")
  @PreAuthorize("@recipeChecker.isAuthor(#id, #principal.getName()) or hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete image of recipe", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Void> deleteImage(@PathVariable Long id, Principal principal) {
    recipeService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }
}
