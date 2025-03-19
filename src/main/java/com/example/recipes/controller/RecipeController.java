package com.example.recipes.controller;

import com.example.recipes.dto.RecipeDto;
import com.example.recipes.entity.Recipe;
import com.example.recipes.mapper.RecipeMapper;
import com.example.recipes.service.RecipeService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    public RecipeController(RecipeService recipeService, RecipeMapper recipeMapper) {
        this.recipeService = recipeService;
        this.recipeMapper = recipeMapper;
    }

    //возможно поменять
    @GetMapping
    public List<RecipeDto> getRecipes(@RequestParam(required = false) String title) {
        List<Recipe> recipes = recipeService.findByTitle(title);
        return recipes.stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    @GetMapping("/all")
    public List<RecipeDto> getAllRecipes() {
        List<Recipe> recipes = recipeService.findAllRecipes();
        return recipes.stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public RecipeDto getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.findById(id);
        return recipeMapper.toDto(recipe);
    }

    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.save(recipe);
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        return recipeService.update(id, recipe);
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.delete(id);
    }
}