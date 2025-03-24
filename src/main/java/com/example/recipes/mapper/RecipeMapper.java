package com.example.recipes.mapper;

import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.RecipeFullDto;
import com.example.recipes.entity.Recipe;
import org.springframework.stereotype.Component;

@Component
public class RecipeMapper {

    private final IngredientMapper ingredientMapper;
    private final ReviewMapper reviewMapper;

    public RecipeMapper(IngredientMapper ingredientMapper, ReviewMapper reviewMapper) {
        this.ingredientMapper = ingredientMapper;
        this.reviewMapper = reviewMapper;
    }

    public RecipeFullDto convertToFullDto(Recipe recipe) {
        RecipeFullDto dto = new RecipeFullDto();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setInstruction(recipe.getInstruction());
        dto.setIngredients(ingredientMapper.convertToDto(recipe.getIngredients()));
        dto.setReviews(reviewMapper.convertToDto(recipe.getReviews()));
        return dto;
    }

    public RecipeDto convertToDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setInstruction(recipe.getInstruction());
        dto.setIngredients(ingredientMapper.convertToDto(recipe.getIngredients()));
        return dto;
    }
}
