package com.example.tangerine.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tangerine.api.domain.User;
import java.util.Optional;

public interface UserService {
  Optional<DecodedJWT> signIn(String username, String password);

  User signUp(User user);

  User update(User user);

  Optional<User> findById(Long userId);

  Optional<User> findByUsername(String username);

  void deleteById(Long userId);
}
