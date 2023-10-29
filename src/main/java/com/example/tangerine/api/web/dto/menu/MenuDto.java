package com.example.tangerine.api.web.dto.menu;

import lombok.Data;

@Data
public class MenuDto {
  private Long id;
  private String name;
  private String createdAt;
  private String pictureUrl;
  private Long authorId;
}
