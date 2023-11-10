package com.example.tangerine.api.web.mapper;

import com.example.tangerine.api.domain.Comment;
import com.example.tangerine.api.web.dto.comment.CommentCreationDto;
import com.example.tangerine.api.web.dto.comment.CommentDto;
import com.example.tangerine.api.web.dto.comment.CommentUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface CommentMapper {
  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "authorUsername", source = "author.username")
  @Mapping(target = "createdAt", expression = "java(comment.getCreatedAt().getEpochSecond())")
  CommentDto toPayload(Comment comment);

  Comment toEntity(CommentCreationDto commentDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Comment partialUpdate(CommentUpdateDto commentDto, @MappingTarget Comment comment);
}
