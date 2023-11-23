package com.example.tangerine.api.data;

import static com.example.tangerine.api.utils.JpaRepositoryUtils.getById;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.tangerine.api.repository.CommentRepository;
import com.example.tangerine.api.repository.IngredientRepository;
import com.example.tangerine.api.repository.MenuRepository;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.testcontainers.TestcontainersInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class EntityLifecycleTest {

  @Autowired
  private MenuRepository menuRepository;
  @Autowired
  private RecipeRepository recipeRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private IngredientRepository ingredientRepository;
  @Autowired
  private CommentRepository commentRepository;

  @Test
  @Sql({"/users-create.sql", "/menus-create.sql", "/recipes-with-ingredients-create.sql"})
  void addRecipeInMenuAddsMenuInRecipe() {
    var menu = getById(1L, menuRepository);
    var recipe = getById(2L, recipeRepository);
    menu.addRecipe(recipe);
    assertThat(recipe.getMenus()).contains(menu);
  }

  @Test
  @Sql({"/users-create.sql", "/menus-create.sql"})
  void deleteMenuDoesNotDeleteCreator() {
    menuRepository.deleteById(1L);

    assertThat(menuRepository.existsById(1L)).isFalse();
    assertThat(userRepository.existsById(1L)).isTrue();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql"})
  void deleteRecipeDoesNotDeleteCreator() {
    recipeRepository.deleteById(1L);

    assertThat(recipeRepository.existsById(1L)).isFalse();
    assertThat(userRepository.existsById(1L)).isTrue();
  }

  @Test
  @Sql({"/users-create.sql", "/menus-create.sql"})
  void deleteUserDeletesCreatedMenus() {
    userRepository.deleteById(1L);

    assertThat(menuRepository.existsById(1L)).isFalse();
  }

  @Test
  @Sql({"/users-create.sql", "/menus-create.sql"})
  void deleteUserDeletesCreatedRecipes() {
    userRepository.deleteById(1L);

    assertThat(recipeRepository.existsById(1L)).isFalse();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql", "/menus-create.sql"})
  void deleteMenuDoesNotDeleteRecipe() {
    var menu = getById(1L, menuRepository);
    var recipe = getById(2L, recipeRepository);
    menu.addRecipe(recipe);

    menuRepository.deleteById(1L);

    assertThat(menuRepository.existsById(1L)).isFalse();
    assertThat(recipeRepository.existsById(2L)).isTrue();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql", "/menus-create.sql"})
  void deleteRecipeInMenuDoesNotDeleteMenu() {
    var menu = getById(1L, menuRepository);
    var recipe = getById(2L, recipeRepository);
    menu.addRecipe(recipe);
    menu.removeRecipe(recipe);

    assertThat(menu.getRecipes()).doesNotContain(recipe);
    assertThat(menuRepository.existsById(1L)).isTrue();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql"})
  void addIngredientInRecipeAddsRecipeInIngredient() {
    var ingredient = getById(1L, ingredientRepository);
    var recipe = getById(2L, recipeRepository);
    recipe.addIngredient(ingredient);
    assertThat(ingredient.getRecipes()).contains(recipe);
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql"})
  void deleteRecipeDoesNotDeleteIngredient() {
    recipeRepository.deleteById(1L);

    assertThat(recipeRepository.existsById(1L)).isFalse();
    assertThat(ingredientRepository.existsById(1L)).isTrue();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql"})
  void deleteIngredientInRecipeDoesNotDeleteRecipe() {
    var ingredient = getById(1L, ingredientRepository);
    var recipe = getById(1L, recipeRepository);
    recipe.removeIngredient(ingredient);

    assertThat(recipeRepository.existsById(1L)).isTrue();
    assertThat(recipe.getIngredients()).doesNotContain(ingredient);
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql", "/comments-create.sql"})
  void addCommentInRecipeAddsRecipeInComment() {
    var comment = getById(1L, commentRepository);
    var recipe = getById(2L, recipeRepository);
    recipe.addComment(comment);
    assertThat(comment.getRecipe().equals(recipe)).isTrue();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql", "/comments-create.sql"})
  void deleteCommentDoesNotDeleteCreator() {
    commentRepository.deleteById(1L);

    assertThat(commentRepository.existsById(1L)).isFalse();
    assertThat(userRepository.existsById(1L)).isTrue();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql"})
  void deleteUserDeletesCreatedComments() {
    userRepository.deleteById(1L);

    assertThat(commentRepository.existsById(1L)).isFalse();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql", "/comments-create.sql"})
  void deleteRecipeDeletesCreatedComments() {
    recipeRepository.deleteById(1L);

    assertThat(recipeRepository.existsById(1L)).isFalse();
    assertThat(commentRepository.existsById(1L)).isFalse();
  }

  @Test
  @Sql({"/users-create.sql", "/recipes-with-ingredients-create.sql", "/comments-create.sql"})
  void deleteCommentInRecipeDoesNotDeleteRecipe() {
    var comment = getById(1L, commentRepository);
    var recipe = getById(1L, recipeRepository);
    recipe.removeComment(comment);

    assertThat(recipeRepository.existsById(1L)).isTrue();
    assertThat(recipe.getComments()).doesNotContain(comment);
  }
}
