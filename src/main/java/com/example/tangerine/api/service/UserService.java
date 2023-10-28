package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.User;
import java.util.Optional;

public interface UserService {
  User create(User user);

  User update(User user);

  Optional<User> findById(Long userId);

  Optional<User> findByUsername(String username);

  void deleteById(Long userId);
}
