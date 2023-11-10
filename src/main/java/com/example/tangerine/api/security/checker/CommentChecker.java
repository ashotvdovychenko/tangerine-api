package com.example.tangerine.api.security.checker;

import com.example.tangerine.api.domain.Comment;
import com.example.tangerine.api.exception.CommentNotFoundException;
import com.example.tangerine.api.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentChecker {
  private final CommentRepository commentRepository;

  public boolean isCommentOrRecipeAuthor(Long id, String username) {
    if (id == null || username == null) {
      return false;
    }
    var comment = getComment(id);
    return comment.getAuthor().getUsername().equals(username)
        || comment.getRecipe().getAuthor().getUsername().equals(username);
  }

  public boolean isCommentAuthor(Long id, String username) {
    if (id == null || username == null) {
      return false;
    }
    return getComment(id).getAuthor().getUsername().equals(username);
  }

  private Comment getComment(Long id) {
    return commentRepository.findById(id).orElseThrow(
        () -> new CommentNotFoundException("Comment with id %s not found".formatted(id))
    );
  }
}
