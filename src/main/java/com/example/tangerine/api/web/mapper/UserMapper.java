package com.example.tangerine.api.web.mapper;

import com.example.tangerine.api.domain.User;
import com.example.tangerine.api.web.dto.user.UserCreationDto;
import com.example.tangerine.api.web.dto.user.UserDto;
import org.mapstruct.Mapper;


@Mapper
public interface UserMapper {
  UserDto toPayload(User user);

  User toEntity(UserCreationDto userDto);
}