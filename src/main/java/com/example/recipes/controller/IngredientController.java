package com.example.recipes.controller;

import com.example.recipes.entity.Ingredient;
import com.example.recipes.service.IngredientService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//не менять
@RestController
@RequestMapping()
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping("/recipes/{recipeId}/ingredients")
    public Ingredient createIngredient(@RequestBody Ingredient ingredient,
                                       @PathVariable Long recipeId) {
        return ingredientService.save(ingredient, recipeId);
    }

    @PutMapping("ingredients/{ingredientId}")
    public Ingredient updateIngredient(@PathVariable Long ingredientId,
                                       @RequestBody Ingredient ingredient) {
        return ingredientService.update(ingredientId, ingredient);
    }

    @DeleteMapping("/recipes/{recipeId}/ingredients/{ingredientId}")
    public void deleteIngredient(@PathVariable Long ingredientId, @PathVariable Long recipeId) {
        ingredientService.delete(ingredientId, recipeId);
    }

    //поменять путь, чтобы было без рецепта
    @GetMapping("/recipes/{recipeId}/ingredients/{ingredientId}")
    public Ingredient findByRecipeId(@PathVariable Long ingredientId,
                                                     @PathVariable Long recipeId) {
        return ingredientService.findById(ingredientId, recipeId);
    }

    @GetMapping("/ingredients/all")
    public List<Ingredient> findAllIngredients() {
        return ingredientService.findAllIngredients();
    }


}



