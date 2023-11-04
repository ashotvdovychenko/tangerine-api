package com.example.tangerine.api.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.domain.User;
import com.example.tangerine.api.repository.RoleRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.security.JwtTokenProvider;
import com.example.tangerine.api.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final AwsS3StorageService storageService;
  @Value("${aws.bucket}")
  private String bucket;

  @Override
  @Transactional
  public Optional<DecodedJWT> signIn(String username, String password) {
    var user = userRepository.findByUsername(username).orElseThrow(
        () -> new IllegalArgumentException("USER NOT FOUND")
    );
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new IllegalArgumentException();
    }
    return jwtTokenProvider.toDecodedJWT(
        jwtTokenProvider.generateToken(user.getId(), username, List.copyOf(user.getRoles())));
  }

  @Override
  @Transactional
  public User signUp(User user) {
    user.addRole(roleRepository.findByName("ROLE_USER").orElseThrow(
        () -> new IllegalArgumentException("ROLE NOT FOUND")
    ));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public User update(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  @Transactional
  public Optional<List<Recipe>> getRecipes(Long id) {
    return userRepository.findById(id).map(User::getRecipes).map(List::copyOf);
  }

  @Override
  @Transactional
  public Optional<List<Menu>> getMenus(Long id) {
    return userRepository.findById(id).map(User::getMenus).map(List::copyOf);
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  @Transactional
  public String addPicture(Long id, MultipartFile file) {
    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException();
    }
    var pictureKey = UUID.randomUUID().toString();
    try {
      storageService.uploadPicture(
          file.getBytes(),
          "user-images/%s/%s".formatted(id, pictureKey),
          bucket);
    } catch (IOException e) {
      throw new RuntimeException();
    }
    userRepository.updatePictureUrlById(id, pictureKey);
    return pictureKey;
  }

  @Override
  public Resource getPicture(Long id) {
    var user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    if (user.getPictureUrl() == null || user.getPictureUrl().isBlank()) {
      throw new IllegalArgumentException();
    }
    return storageService.findByKey(
        "user-images/%s/%s".formatted(user.getId(), user.getPictureUrl()),
        bucket);
  }

  @Override
  @Transactional
  public void deletePicture(Long id) {
    var user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    if (user.getPictureUrl() != null) {
      storageService.deleteByKey(
          "user-images/%s/%s".formatted(user.getId(), user.getPictureUrl()),
          bucket
      );
      user.setPictureUrl(null);
    }
  }
}
