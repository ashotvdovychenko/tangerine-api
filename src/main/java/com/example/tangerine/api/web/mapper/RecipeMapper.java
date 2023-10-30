package com.example.tangerine.api.web.mapper;

import com.example.tangerine.api.domain.Recipe;
import com.example.tangerine.api.web.dto.recipe.RecipeCreationDto;
import com.example.tangerine.api.web.dto.recipe.RecipeDto;
import com.example.tangerine.api.web.dto.recipe.RecipeUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface RecipeMapper {
  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "createdAt", expression = "java(recipe.getCreatedAt().getEpochSecond())")
  RecipeDto toPayload(Recipe recipe);

  Recipe toEntity(RecipeCreationDto recipeDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Recipe partialUpdate(RecipeUpdateDto recipeDto, @MappingTarget Recipe recipe);
}
