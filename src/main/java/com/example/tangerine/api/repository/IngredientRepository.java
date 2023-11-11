package com.example.tangerine.api.repository;

import com.example.tangerine.api.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
  @Modifying
  @Query("UPDATE Ingredient i SET i.imageKey = :imageKey where i.id = :id")
  void updateImageKeyById(Long id, String imageKey);
}