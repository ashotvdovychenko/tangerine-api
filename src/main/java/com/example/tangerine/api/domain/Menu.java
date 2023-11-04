package com.example.tangerine.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Entity
@Table(name = "MENUS")
public class Menu {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "created_at")
  private Instant createdAt = Instant.now();

  @Column(name = "picture_url")
  private String pictureUrl;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToMany
  @JoinTable(name = "menus_recipes",
      joinColumns = @JoinColumn(name = "menu_id"),
      inverseJoinColumns = @JoinColumn(name = "recipe_id"))
  private Set<Recipe> recipes = new HashSet<>();

  public void addRecipe(Recipe recipe) {
    this.recipes.add(recipe);
    recipe.getMenus().add(this);
  }

  public void removeRecipe(Recipe recipe) {
    this.recipes.remove(recipe);
    recipe.getMenus().remove(this);
  }

  public void removeRecipes(List<Recipe> recipes) {
    recipes.forEach(this::removeRecipe);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Menu menu = (Menu) o;
    return getId() != null && Objects.equals(getId(), menu.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}