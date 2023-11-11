package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.IngredientService;
import com.example.tangerine.api.web.dto.ExceptionResponse;
import com.example.tangerine.api.web.dto.ingredient.IngredientCreationDto;
import com.example.tangerine.api.web.dto.ingredient.IngredientDto;
import com.example.tangerine.api.web.dto.ingredient.IngredientUpdateDto;
import com.example.tangerine.api.web.mapper.IngredientMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Ingredient Controller")
@CrossOrigin
@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

  private final IngredientService ingredientService;
  private final IngredientMapper ingredientMapper;

  @GetMapping
  @Operation(summary = "Get all ingredients", responses = @ApiResponse(responseCode = "200",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class)))))
  public ResponseEntity<List<IngredientDto>> findAll() {
    return ingredientService.findAll().stream()
        .map(ingredientMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get ingredient by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IngredientDto.class))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<IngredientDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(ingredientService.findById(id).map(ingredientMapper::toPayload));
  }

  @GetMapping("/{id}/image")
  @Operation(summary = "Get image of ingredient", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Resource> getImage(@PathVariable Long id) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(ingredientService.getImage(id));
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Create new ingredient", responses = {
      @ApiResponse(responseCode = "201",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IngredientDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content)
  })
  public ResponseEntity<IngredientDto> create(@RequestBody @Valid
                                              IngredientCreationDto ingredientDto) {
    var created = ingredientService.create(ingredientMapper.toEntity(ingredientDto));
    return new ResponseEntity<>(ingredientMapper.toPayload(created), HttpStatus.CREATED);
  }

  @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Add image to ingredient", responses = {
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
                                            @RequestPart MultipartFile file) {
    var imageKey = ingredientService.addImage(id, file);
    return new ResponseEntity<>(imageKey, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Update ingredient by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IngredientDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<IngredientDto> update(@RequestBody @Valid IngredientUpdateDto ingredientDto,
                                              @PathVariable Long id) {
    return ResponseEntity.of(ingredientService.findById(id)
        .map(menu -> ingredientMapper.partialUpdate(ingredientDto, menu))
        .map(ingredientService::update)
        .map(ingredientMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete ingredient by id", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content)
  })
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    ingredientService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/image")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete image of ingredient by id", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
    ingredientService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }
}
