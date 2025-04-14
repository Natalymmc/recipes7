package com.example.recipes.service;

import com.example.recipes.dto.IngredientDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.exceptions.NotFoundException;
import com.example.recipes.exceptions.ValidationException;
import com.example.recipes.mapper.IngredientMapper;
import com.example.recipes.repository.IngredientRepository;
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

    //было tr
    public void deleteIngredient(Long ingredientId) {
        if (ingredientId == null || ingredientId <= 0) {
            throw new ValidationException("Ingredient ID must be greater than 0.");
        }
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
        for (Recipe recipe : ingredient.getRecipes()) {
            recipe.getIngredients().remove(ingredient);
        }
        ingredientRepository.delete(ingredient);
    }

    @Transactional
    public IngredientDto createIngredient(IngredientDto ingredientDto) {
        // Проверка на null или пустое имя
        if (ingredientDto.getName() == null || ingredientDto.getName().trim().isEmpty()) {
            throw new ValidationException("Ingredient name cannot be null or empty.");
        }

        // Удаление лишних пробелов и оставление только одиночных пробелов между словами
        String trimmedName = ingredientDto.getName().trim().replaceAll("\\s+", " ");

        // Проверка на существование ингредиента с таким именем
        if (ingredientRepository.findByName(trimmedName).isPresent()) {
            throw new ValidationException("Ingredient with name '"
                    + trimmedName + "' already exists.");
        }

        // Создание нового ингредиента
        Ingredient ingredient = new Ingredient();
        ingredient.setName(trimmedName); // Использование очищенного имени
        ingredient = ingredientRepository.save(ingredient);

        // Установка ID и возврат DTO
        ingredientDto.setId(ingredient.getId());
        ingredientDto.setName(trimmedName); // Обновление имени в DTO
        return ingredientDto;
    }

    @Transactional
    public IngredientDto updateIngredient(Long id, IngredientDto ingredientDto) {
        if (id == null || id <= 0) {
            throw new ValidationException("Ingredient ID must be greater than 0.");
        }

        if (ingredientDto.getName() == null || ingredientDto.getName().trim().isEmpty()) {
            throw new ValidationException("Ingredient name cannot be null or empty.");
        }

        // Удаление лишних пробелов и оставление только одиночных пробелов между словами
        String trimmedName = ingredientDto.getName().trim().replaceAll("\\s+", " ");

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Ingredient with ID " + id + " not found."));

        // Проверка на существование ингредиента с тем же именем, если имя изменилось
        if (!ingredient.getName().equals(trimmedName)
                && ingredientRepository.findByName(trimmedName).isPresent()) {
            throw new ValidationException("Ingredient with name '"
                    + trimmedName + "' already exists.");
        }

        ingredient.setName(trimmedName); // Использование очищенного имени
        ingredientRepository.save(ingredient);

        return ingredientMapper.convertToDto(ingredient);
    }
}