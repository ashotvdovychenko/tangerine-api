package com.example.tangerine.api.service;

import com.example.tangerine.api.domain.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentService {
  Comment create(Comment comment, Long recipeId, String username);

  Comment update(Comment comment);

  List<Comment> findAll();

  Optional<Comment> findById(Long commentId);

  void deleteById(Long commentId);
}