package com.example.recipes.controller;

import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.RecipeFullDto;
import com.example.recipes.service.RecipeService;
import java.util.List;
import org.springframework.http.ResponseEntity;
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

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecipeFullDto>> getAllRecipes() {
        List<RecipeFullDto> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @GetMapping
    public ResponseEntity<List<RecipeDto>> getRecipesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(recipeService.findRecipesByTitle(title));
    }

    @PostMapping
    public ResponseEntity<RecipeFullDto> createRecipe(@RequestBody RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.createRecipe(recipeDto));
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeFullDto> updateRecipe(@PathVariable Long recipeId,
                                                      @RequestBody RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId, recipeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipeById(id);
        return ResponseEntity.noContent().build();
    }

}