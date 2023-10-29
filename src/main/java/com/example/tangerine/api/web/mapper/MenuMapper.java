package com.example.tangerine.api.web.mapper;

import com.example.tangerine.api.domain.Menu;
import com.example.tangerine.api.web.dto.menu.MenuCreationDto;
import com.example.tangerine.api.web.dto.menu.MenuDto;
import com.example.tangerine.api.web.dto.menu.MenuUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface MenuMapper {
  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "createdAt", source = "createdAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
  MenuDto toPayload(Menu menu);

  Menu toEntity(MenuCreationDto menuDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Menu partialUpdate(MenuUpdateDto menuDto, @MappingTarget Menu menu);
}
