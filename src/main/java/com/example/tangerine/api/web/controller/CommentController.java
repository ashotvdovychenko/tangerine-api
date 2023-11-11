package com.example.tangerine.api.web.controller;

import com.example.tangerine.api.service.CommentService;
import com.example.tangerine.api.web.dto.ExceptionResponse;
import com.example.tangerine.api.web.dto.comment.CommentDto;
import com.example.tangerine.api.web.dto.comment.CommentUpdateDto;
import com.example.tangerine.api.web.mapper.CommentMapper;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment Controller")
@CrossOrigin
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;
  private final CommentMapper commentMapper;

  @GetMapping
  @Operation(summary = "Get all comments", responses = @ApiResponse(responseCode = "200",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))))
  public ResponseEntity<List<CommentDto>> findAll() {
    return commentService.findAll().stream()
        .map(commentMapper::toPayload)
        .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get comment by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CommentDto.class))),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<CommentDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(commentService.findById(id).map(commentMapper::toPayload));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("@commentChecker.isCommentAuthor(#id, #principal.getName())")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Update comment by id", responses = {
      @ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CommentDto.class))),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<CommentDto> update(@PathVariable Long id,
                                           @RequestBody @Valid CommentUpdateDto commentDto,
                                           Principal principal) {
    return ResponseEntity.of(commentService.findById(id)
        .map(menu -> commentMapper.partialUpdate(commentDto, menu))
        .map(commentService::update)
        .map(commentMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@commentChecker.isCommentOrRecipeAuthor(#id, #principal.getName()) "
      + "or hasRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "bearer_token")
  @Operation(summary = "Delete comment by id", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content)
  })
  public ResponseEntity<Void> deleteById(@PathVariable Long id,
                                         Principal principal) {
    commentService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
