package com.example.tangerine.api.security.checker;

import com.example.tangerine.api.exception.MenuNotFoundException;
import com.example.tangerine.api.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuChecker {
  private final MenuRepository menuRepository;

  public boolean isAuthor(Long id, String username) {
    if (id == null || username == null) {
      return false;
    }
    var menu = menuRepository.findById(id).orElseThrow(
        () -> new MenuNotFoundException("Menu with id %s not found".formatted(id))
    );
    return menu.getAuthor().getUsername().equals(username);
  }
}
