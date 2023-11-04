package com.example.tangerine.api.repository;

import com.example.tangerine.api.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  @Modifying
  @Query("UPDATE User u SET u.pictureUrl = :pictureUrl where u.id = :id")
  void updatePictureUrlById(Long id, String pictureUrl);
}
