package com.example.tangerine.api.web.dto.menu;

import lombok.Data;

@Data
public class MenuDto {
  private Long id;
  private String name;
  private Long createdAt;
  private String imageKey;
  private Long authorId;
  private String authorUsername;
}
