package com.example.tangerine.api.security.checker;

import com.example.tangerine.api.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuChecker {
  private final MenuRepository menuRepository;

  public boolean check(Long id, String username) {
    if (id == null || username == null) {
      return false;
    }
    var menu = menuRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    return menu.getAuthor().getUsername().equals(username);
  }
}
