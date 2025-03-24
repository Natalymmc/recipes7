package com.example.recipes.controller;

import com.example.recipes.dto.IngredientDto;
import com.example.recipes.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long ingredientId) {
        ingredientService.deleteIngredient(ingredientId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<IngredientDto>
        createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ResponseEntity.ok(ingredientService.createIngredient(ingredientDto));
    }

    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientDto> updateIngredient(
            @PathVariable Long ingredientId,
            @RequestBody IngredientDto ingredientDto) {
        IngredientDto updatedIngredient = ingredientService
                .updateIngredient(ingredientId, ingredientDto);
        return ResponseEntity.ok(updatedIngredient);
    }
}


