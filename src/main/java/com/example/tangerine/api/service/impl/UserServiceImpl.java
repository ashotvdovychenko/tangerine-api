package com.example.tangerine.api.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.domain.User;
import com.example.tangerine.api.exception.ImageNotFoundException;
import com.example.tangerine.api.exception.ImageUploadException;
import com.example.tangerine.api.exception.InvalidPasswordException;
import com.example.tangerine.api.exception.RoleNotFoundException;
import com.example.tangerine.api.exception.UserAlreadyExistsException;
import com.example.tangerine.api.exception.UserNotFoundException;
import com.example.tangerine.api.repository.RoleRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.security.JwtTokenProvider;
import com.example.tangerine.api.service.StorageService;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final StorageService storageService;
  @Value("${aws.bucket}")
  private String bucket;

  @Override
  @Transactional
  public Optional<DecodedJWT> signIn(String username, String password) {
    var user = userRepository.findByUsername(username).orElseThrow(
        () -> new UserNotFoundException("User with username %s not found".formatted(username))
    );
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new InvalidPasswordException("Invalid password");
    }
    return jwtTokenProvider.toDecodedJWT(
        jwtTokenProvider.generateToken(user.getId(), username, List.copyOf(user.getRoles())));
  }

  @Override
  @Transactional
  public User signUp(User user) {
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new UserAlreadyExistsException(
          "Username %s is already in use".formatted(user.getUsername()));
    }
    user.addRole(roleRepository.findByName("ROLE_USER").orElseThrow(
        () -> new RoleNotFoundException("Role 'USER' not found. Failed to assign to new user")
    ));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public User update(User user, String newPassword) {
    if (isUsernameInUse(user)) {
      throw new UserAlreadyExistsException(
          "Username %s is already in use".formatted(user.getUsername()));
    }
    if (newPassword != null) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(user);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> findById(Long userId) {
    return userRepository.findById(userId);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  @Transactional
  public Optional<List<Recipe>> getRecipes(Long userId) {
    return userRepository.findById(userId).map(User::getRecipes).map(List::copyOf);
  }

  @Override
  @Transactional
  public Optional<List<Menu>> getMenus(Long userId) {
    return userRepository.findById(userId).map(User::getMenus).map(List::copyOf);
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  @Transactional
  public String addImage(Long userId, MultipartFile file) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException("User with id %s not found".formatted(userId));
    }
    var imageKey = UUID.randomUUID().toString();
    try {
      storageService.uploadImage(
          file.getBytes(),
          "user-images/%s/%s".formatted(userId, imageKey),
          bucket);
    } catch (IOException e) {
      var fileName = file.getOriginalFilename();
      throw new ImageUploadException("Failed to upload image %s".formatted(fileName));
    }
    userRepository.updateImageKeyById(userId, imageKey);
    return imageKey;
  }

  @Override
  public Resource getImage(Long userId) {
    var user = userRepository.findById(userId).orElseThrow(
        () -> new UserNotFoundException("User with id %s not found".formatted(userId))
    );
    if (user.getImageKey() == null || user.getImageKey().isBlank()) {
      throw new ImageNotFoundException("Image of user with id %s not found".formatted(userId));
    }
    return storageService.findByKey(
        "user-images/%s/%s".formatted(user.getId(), user.getImageKey()),
        bucket);
  }

  @Override
  @Transactional
  public void deleteImage(Long userId) {
    var user = userRepository.findById(userId).orElseThrow(
        () -> new UserNotFoundException("User with id %s not found".formatted(userId))
    );
    if (user.getImageKey() != null) {
      storageService.deleteByKey(
          "user-images/%s/%s".formatted(user.getId(), user.getImageKey()),
          bucket
      );
      user.setImageKey(null);
    }
  }

  private boolean isUsernameInUse(User user) {
    return userRepository.findByUsername(user.getUsername())
        .filter(found -> !found.getId().equals(user.getId())).isPresent();
  }
}
