package com.example.tangerine.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Entity
@Table(name = "RECIPES")
public class Recipe {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "seconds_duration")
  private Long secondsDuration;

  @Column(name = "products_cost")
  private Long productsCost;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "picture_url")
  private String pictureUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "complexity")
  private Complexity complexity;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(optional = false)
  @JoinColumn(name = "menu_id", nullable = false)
  private Menu menu;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Recipe recipe = (Recipe) o;
    return getId() != null && Objects.equals(getId(), recipe.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}