package com.example.tangerine.api.repository;

import com.example.tangerine.api.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
  @Modifying
  @Query("UPDATE Menu m SET m.imageKey = :imageKey where m.id = :id")
  void updateImageKeyById(Long id, String imageKey);
}
