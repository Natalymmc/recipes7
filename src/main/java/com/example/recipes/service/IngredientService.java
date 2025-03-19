package com.example.recipes.service;

import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.repository.IngredientRepository;
import com.example.recipes.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {

    private static final String ERROR_MESSAGE = "Ingredient not found";

    private final IngredientRepository ingredientRepository;
    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;

    public IngredientService(IngredientRepository ingredientRepository,
                             RecipeService recipeService, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
    }

    public Ingredient findById(Long id, Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new EntityNotFoundException("Recipe not found");

        }

        Recipe recipe = recipeService.findById(recipeId);
        List<Ingredient> ingredients = recipe.getIngredients();
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(ERROR_MESSAGE));
        if (ingredients.contains(ingredient)) {
            return ingredient;
        } else {
            throw new EntityNotFoundException(ERROR_MESSAGE);
        }
    }

    public List<Ingredient> findAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient save(Ingredient ingredient, Long recipeId) {

        Recipe recipe = recipeService.findById(recipeId);

        if (recipe == null) {
            throw new EntityNotFoundException("Recipe not found");
        }

        List<Recipe> newRecipes = new ArrayList<>();

        if (ingredientRepository.existsByName(ingredient.getName())) {
            ingredient = ingredientRepository.findByName(ingredient.getName());
            newRecipes = ingredient.getRecipes();
        }

        List<Ingredient> ingredients = recipe.getIngredients();
        if (!ingredients.contains(ingredient)) {
            ingredients.add(ingredient);
            recipe.setIngredients(ingredients);
            newRecipes.add(recipe);
            ingredient.setRecipes(newRecipes);
        }

        return ingredientRepository.save(ingredient);
    }

    public Ingredient update(Long id, Ingredient ingredient) {
        if (!ingredientRepository.existsById(id)) {
            throw new EntityNotFoundException(ERROR_MESSAGE);
        }
        ingredient.setId(id);
        return ingredientRepository.save(ingredient);
    }

    public void delete(Long id, Long recipeId) {

        Recipe recipe = recipeService.findById(recipeId);
        List<Ingredient> ingredients = recipe.getIngredients();
        Ingredient ingredient = findById(id, recipeId);

        ingredients.remove(ingredient);

        recipe.setIngredients(ingredients);
        recipeService.update(recipeId, recipe);

        List<Recipe> recipes = ingredient.getRecipes();
        recipes.remove(recipe);
        if (recipes.isEmpty()) {
            ingredientRepository.delete(ingredient);
        } else {
            ingredient.setRecipes(recipes);
            update(id, ingredient);
        }
    }
}