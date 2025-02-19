package com.example.recipes.controller;

import com.example.recipes.model.Recipe;
import com.example.recipes.service.RecipeService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    @GetMapping
    public List<Recipe> getRecipes(@RequestParam(required = false) String title) {
        return recipeService.getRecipesByTitle(title);
    }

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }
}

