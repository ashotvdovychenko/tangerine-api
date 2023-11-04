package com.example.tangerine.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
  Optional<DecodedJWT> signIn(String username, String password);

  User signUp(User user);

  User update(User user);

  List<User> findAll();

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  Optional<List<Recipe>> getRecipes(Long id);

  Optional<List<Menu>> getMenus(Long id);

  void deleteById(Long id);

  String addPicture(Long id, MultipartFile file);

  Resource getPicture(Long id);

  void deletePicture(Long id);
}
