package com.example.recipes.service;

import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.entity.Review;
import com.example.recipes.repository.IngredientRepository;
import com.example.recipes.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Recipe> findByTitle(String title) {
        List<Recipe> recipes = recipeRepository.findByTitle(title);
        if (recipes.isEmpty()) {
            throw new EntityNotFoundException("No recipes found with title: " + title);
        }
        return recipes;
    }

    public List<Recipe> findAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe findById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Recipe not found"));
    }

    public Recipe save(Recipe recipe) {

        if (recipe.getIngredients() != null) {
            List<Ingredient> saveIngredients = new ArrayList<>();

            for (Ingredient ingredient : recipe.getIngredients()) {

                if (ingredientRepository.existsByName(ingredient.getName())) {
                    Ingredient existingIngredient =
                            ingredientRepository.findByName(ingredient.getName());
                    saveIngredients.add(existingIngredient);
                } else {
                    saveIngredients.add(ingredientRepository.save(ingredient));
                }
            }

            recipe.setIngredients(saveIngredients);
        }

        for (Review review : recipe.getReviews()) {
            review.setRecipe(recipe);
        }

        return recipeRepository.save(recipe);
    }

    public Recipe update(Long id, Recipe recipe) {
        if (!recipeRepository.existsById(id)) {
            throw new EntityNotFoundException("Recipe not found");
        }
        recipe.setId(id);
        return recipeRepository.save(recipe);
    }

    public void delete(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new EntityNotFoundException("Recipe not found with ID: " + id);
        }
        recipeRepository.deleteById(id);
    }
}