package com.example.tangerine.api.security.checker;

import com.example.tangerine.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserChecker {
  private final UserRepository userRepository;

  public boolean check(Long id, String username) {
    if (id == null || username == null) {
      return false;
    }
    var user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    return user.getUsername().equals(username);
  }
}
