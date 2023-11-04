package com.example.tangerine.api.web.mapper;

import com.example.tangerine.api.domain.User;
import com.example.tangerine.api.web.dto.user.UserCreationDto;
import com.example.tangerine.api.web.dto.user.UserDto;
import com.example.tangerine.api.web.dto.user.UserUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper
public interface UserMapper {
  @Mapping(target = "createdAt", expression = "java(user.getCreatedAt().getEpochSecond())")
  UserDto toPayload(User user);

  User toEntity(UserCreationDto userDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  User partialUpdate(UserUpdateDto userDto, @MappingTarget User user);
}