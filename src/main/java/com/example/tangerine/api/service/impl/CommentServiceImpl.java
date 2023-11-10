package com.example.tangerine.api.service.impl;

import com.example.tangerine.api.domain.Comment;
import com.example.tangerine.api.exception.RecipeNotFoundException;
import com.example.tangerine.api.exception.UserNotFoundException;
import com.example.tangerine.api.repository.CommentRepository;
import com.example.tangerine.api.repository.RecipeRepository;
import com.example.tangerine.api.repository.UserRepository;
import com.example.tangerine.api.service.CommentService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  private final UserRepository userRepository;

  private final RecipeRepository recipeRepository;

  @Override
  @Transactional
  public Comment create(Comment comment, Long recipeId, String username) {
    comment.setAuthor(userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(
            "User with username %s not found".formatted(username))));
    comment.setRecipe(recipeRepository.findById(recipeId)
        .orElseThrow(() -> new RecipeNotFoundException(
            "Recipe with id %s not found".formatted(recipeId)
        )));
    return commentRepository.save(comment);
  }

  @Override
  public Comment update(Comment comment) {
    return commentRepository.save(comment);
  }

  @Override
  public List<Comment> findAll() {
    return commentRepository.findAll();
  }

  @Override
  public Optional<Comment> findById(Long commentId) {
    return commentRepository.findById(commentId);
  }

  @Override
  public void deleteById(Long commentId) {
    commentRepository.deleteById(commentId);
  }
}
