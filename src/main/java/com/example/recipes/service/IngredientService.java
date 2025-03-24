package com.example.recipes.service;

import com.example.recipes.dto.IngredientDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.mapper.IngredientMapper;
import com.example.recipes.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;


    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = new IngredientMapper();
    }

    @Transactional
    public void deleteIngredient(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found"));
        for (Recipe recipe : ingredient.getRecipes()) {
            recipe.getIngredients().remove(ingredient);
        }
        ingredientRepository.delete(ingredient);
    }

    @Transactional
    public IngredientDto createIngredient(IngredientDto ingredientDto) {
        if (ingredientRepository
                .findByName(ingredientDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Ingredient with name '"
                    + ingredientDto.getName() + "' already exists");
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDto.getName());
        ingredient = ingredientRepository.save(ingredient);
        ingredientDto.setId(ingredient.getId());
        return ingredientDto;
    }

    @Transactional
    public IngredientDto updateIngredient(Long id, IngredientDto ingredientDto) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found"));

        ingredient.setName(ingredientDto.getName());
        ingredientRepository.save(ingredient);

        return ingredientMapper.convertToDto(ingredient);
    }


}