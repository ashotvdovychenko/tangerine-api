package com.example.tangerine.api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
  private Instant createdAt = Instant.now();

  @Column(name = "image_key")
  private String imageKey;

  @Enumerated(EnumType.STRING)
  @Column(name = "complexity")
  private Complexity complexity;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToMany(mappedBy = "recipes")
  private Set<Menu> menus = new LinkedHashSet<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  public void addComment(Comment comment) {
    this.comments.add(comment);
    comment.setRecipe(this);
  }

  public void removeComment(Comment comment) {
    this.comments.remove(comment);
    comment.setRecipe(null);
  }

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