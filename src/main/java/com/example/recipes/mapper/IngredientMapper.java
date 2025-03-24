package com.example.recipes.mapper;

import com.example.recipes.dto.IngredientDto;
import com.example.recipes.entity.Ingredient;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class IngredientMapper {
    public Set<IngredientDto> convertToDto(Set<Ingredient> ingredients) {
        if (ingredients == null) {
            return new HashSet<>();
        }
        return ingredients.stream().map(ingredient -> {
            IngredientDto ingredientDto = new IngredientDto();
            ingredientDto.setId(ingredient.getId());
            ingredientDto.setName(ingredient.getName());
            return ingredientDto;
        }).collect(Collectors.toSet());
    }

    public IngredientDto convertToDto(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }
        IngredientDto dto = new IngredientDto();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        return dto;
    }

}

