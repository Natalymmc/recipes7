package com.example.recipes.service;

import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.RecipeFullDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.mapper.RecipeMapper;
import com.example.recipes.repository.IngredientRepository;
import com.example.recipes.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

    private static final String RECIPE_NOT_FOUND = "Recipe not found";

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository,
                         IngredientRepository ingredientRepository, RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeMapper = recipeMapper;

    }

    public List<RecipeFullDto> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(recipeMapper::convertToFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecipeDto getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RECIPE_NOT_FOUND));
        return recipeMapper.convertToDto(recipe);
    }

    public List<RecipeDto> findRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(recipeMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecipeFullDto createRecipe(RecipeDto recipeDto) {

        Recipe recipe = new Recipe(recipeDto.getTitle(),
                recipeDto.getDescription(), recipeDto.getInstruction());

        Set<Ingredient> ingredients = recipeDto.getIngredients().stream().map(ingredientDto -> {

            return ingredientRepository.findByName(ingredientDto.getName())
                    .orElseGet(() -> {

                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(ingredientDto.getName());
                        return ingredientRepository.save(ingredient);
                    });
        }).collect(Collectors.toSet());

        recipe.setIngredients(ingredients);

        recipeRepository.save(recipe);

        return recipeMapper.convertToFullDto(recipe);
    }

    public RecipeFullDto updateRecipe(Long recipeId, RecipeDto recipeDto) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException(RECIPE_NOT_FOUND));
        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setInstruction(recipeDto.getInstruction());
        recipeRepository.save(recipe);
        return recipeMapper.convertToFullDto(recipe);
    }

    @Transactional
    public void deleteRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RECIPE_NOT_FOUND));

        recipe.getIngredients().clear();
        recipeRepository.save(recipe);

        recipeRepository.delete(recipe);
    }
}
