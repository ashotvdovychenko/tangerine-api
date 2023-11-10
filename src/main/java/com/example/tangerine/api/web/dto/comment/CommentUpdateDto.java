package com.example.tangerine.api.web.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentUpdateDto {
  @NotBlank(message = "Specify comment text")
  @Size(max = 5096, message = "Enter less than 5096 characters")
  private String text;
}
