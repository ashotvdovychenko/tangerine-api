package com.example.tangerine.api.repository;

import com.example.tangerine.api.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
  @Modifying
  @Query("UPDATE Menu r SET r.pictureUrl = :pictureUrl where r.id = :id")
  void updatePictureUrlById(Long id, String pictureUrl);
}
