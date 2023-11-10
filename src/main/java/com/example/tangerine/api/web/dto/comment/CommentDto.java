package com.example.tangerine.api.web.dto.comment;

import lombok.Data;

@Data
public class CommentDto {
  private Long id;
  private String text;
  private Long createdAt;
  private String authorUsername;
  private String authorId;
}
