package com.example.tangerine.api.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tangerine.api.domain.User;
import com.example.tangerine.api.repository.RoleRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.security.JwtTokenProvider;
import com.example.tangerine.api.service.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

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
        jwtTokenProvider.generateToken(username, List.copyOf(user.getRoles())));
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
    return userRepository.save(user);
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
  public void deleteById(Long userId) {
    userRepository.deleteById(userId);
  }
}
