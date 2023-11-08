package com.example.tangerine.api.repository;

import com.example.tangerine.api.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
  @Modifying
  @Query("UPDATE Recipe r SET r.imageKey = :imageKey where r.id = :id")
  void updateImageKeyById(Long id, String imageKey);
}
