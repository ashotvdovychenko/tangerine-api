package com.example.tangerine.api.web.mapper;

import com.example.tangerine.api.domain.Ingredient;
import com.example.tangerine.api.web.dto.ingredient.IngredientCreationDto;
import com.example.tangerine.api.web.dto.ingredient.IngredientDto;
import com.example.tangerine.api.web.dto.ingredient.IngredientUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface IngredientMapper {
  @Mapping(target = "createdAt", expression = "java(ingredient.getCreatedAt().getEpochSecond())")
  IngredientDto toPayload(Ingredient ingredient);

  Ingredient toEntity(IngredientCreationDto ingredientDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Ingredient partialUpdate(IngredientUpdateDto ingredientDto, @MappingTarget Ingredient ingredient);
}
