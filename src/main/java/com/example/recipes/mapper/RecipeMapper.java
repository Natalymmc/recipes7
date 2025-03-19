package com.example.recipes.mapper;

import com.example.recipes.dto.IngredientDto;
import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.ReviewDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.entity.Review;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RecipeMapper {
    private final IngredientMapper ingredientMapper;
    private final ReviewMapper reviewMapper;

    public RecipeMapper(IngredientMapper ingredientMapper, ReviewMapper reviewMapper) {
        this.ingredientMapper = ingredientMapper;
        this.reviewMapper = reviewMapper;
    }

    public RecipeDto toDto(Recipe recipe) {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle(recipe.getTitle());

        if (recipe.getIngredients() != null) {
            List<IngredientDto> ingredientsDto = recipe.getIngredients().stream()
                    .map(ingredientMapper::toDto)
                    .toList();
            recipeDto.setIngredients(ingredientsDto);
        }

        if (recipe.getReviews() != null) {
            List<ReviewDto> reviewsDto = recipe.getReviews().stream()
                    .map(reviewMapper::toDto)
                    .toList();
            recipeDto.setReviews(reviewsDto);
        }

        return recipeDto;
    }

    public Recipe toEntity(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeDto.getTitle());

        if (recipeDto.getIngredients() != null) {
            List<Ingredient> ingredients = recipeDto.getIngredients().stream()
                    .map(ingredientMapper::toEntity)
                    .toList();
            recipe.setIngredients(ingredients);
        }

        if (recipeDto.getReviews() != null) {
            List<Review> reviews = recipeDto.getReviews().stream()
                    .map(reviewMapper::toEntity)
                    .toList();
            recipe.setReviews(reviews);
        }

        return recipe;
    }
}
