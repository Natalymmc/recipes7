package com.example.recipes.service;

import com.example.recipes.config.CacheConfig;
import com.example.recipes.dto.IngredientDto;
import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.RecipeFullDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.exceptions.NotFoundException;
import com.example.recipes.exceptions.ValidationException;
import com.example.recipes.mapper.RecipeMapper;
import com.example.recipes.repository.IngredientRepository;
import com.example.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private CacheConfig cacheService;

    @Test
    void getAllRecipes_success() {
        // Arrange
        List<Recipe> recipes = List.of(new Recipe(1L, "Recipe 1", "Description 1", "Instruction 1"));
        List<RecipeFullDto> recipeDtos = List.of(new RecipeFullDto());

        recipeDtos.get(0).setId(1L);
        recipeDtos.get(0).setTitle("Recipe 1");
        recipeDtos.get(0).setDescription("Description 1");
        recipeDtos.get(0).setInstruction("Instruction 1");

        when(recipeRepository.findAll()).thenReturn(recipes);
        when(recipeMapper.convertToFullDto(any(Recipe.class))).thenReturn(recipeDtos.get(0));

        // Act
        List<RecipeFullDto> result = recipeService.getAllRecipes();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Recipe 1", result.get(0).getTitle());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void getAllRecipes_emptyList_success() {
        // Arrange
        when(recipeRepository.findAll()).thenReturn(List.of());

        // Act
        List<RecipeFullDto> result = recipeService.getAllRecipes();

        // Assert
        assertTrue(result.isEmpty());
        verify(recipeRepository, times(1)).findAll();
    }


    @Test
    void getRecipeById_fromCache_success() {
        // Arrange
        Long recipeId = 1L;
        RecipeDto cachedRecipe = new RecipeDto();
        cachedRecipe.setTitle("Cached Recipe");
        String cacheKey = "recipe_" + recipeId;

        when(cacheService.containsKey(cacheKey)).thenReturn(true);
        when(cacheService.get(cacheKey)).thenReturn(cachedRecipe);

        // Act
        RecipeDto result = recipeService.getRecipeById(recipeId);

        // Assert
        assertEquals("Cached Recipe", result.getTitle());
        verify(cacheService, times(1)).get(cacheKey);
        verifyNoInteractions(recipeRepository);
    }

    @Test
    void getRecipeById_notFound_throwsNotFoundException() {
        // Arrange
        Long recipeId = 100L;
        String cacheKey = "recipe_" + recipeId;

        when(cacheService.containsKey(cacheKey)).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> recipeService.getRecipeById(recipeId));
        assertEquals("Recipe not found with ID " + recipeId, exception.getMessage());
        verify(cacheService, times(1)).containsKey(cacheKey);
        verify(recipeRepository, times(1)).findById(recipeId);
    }

    @Test
    void getRecipeById_updatesCache_success() {
        // Arrange
        Long recipeId = 4L;
        Recipe recipe = new Recipe(recipeId, "Recipe From DB", "Description", "Instruction");
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Recipe From DB");
        String cacheKey = "recipe_" + recipeId;

        when(cacheService.containsKey(cacheKey)).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.convertToDto(recipe)).thenReturn(recipeDto);

        // Act
        recipeService.getRecipeById(recipeId);

        // Assert
        verify(cacheService, times(1)).put(cacheKey, recipeDto); // Убедимся, что рецепт сохраняется в кэш
    }


    @Test
    void getRecipeById_invalidId_throwsValidationException() {
        // Arrange
        Long invalidId = -1L; // Некорректный ID

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.getRecipeById(invalidId));
        assertEquals("Recipe ID must be greater than 0.", exception.getMessage());
    }


    @Test
    void createRecipe_success() {
        // Arrange
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("New Recipe");
        recipeDto.setDescription("Description");
        recipeDto.setInstruction("Instruction");

        // Указываем валидный ингредиент с именем
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Salt");
        recipeDto.setIngredients(Set.of(ingredientDto));

        Recipe recipe = new Recipe("New Recipe", "Description", "Instruction");
        recipe.setIngredients(new HashSet<>());

        Ingredient ingredient = new Ingredient("Salt");

        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(recipeMapper.convertToDto(any(Recipe.class))).thenReturn(recipeDto); // Изменено здесь

        // Act
        RecipeDto result = recipeService.createRecipe(recipeDto);

        // Assert
        assertEquals("New Recipe", result.getTitle());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        verify(recipeMapper, times(1)).convertToDto(any(Recipe.class)); // Добавьте эту проверку
    }

    @Test
    void createRecipe_emptyTitle_throwsValidationException() {
        // Arrange
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle(""); // Некорректное название
        recipeDto.setIngredients(Set.of(new IngredientDto()));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.createRecipe(recipeDto));
        assertEquals("Recipe title cannot be null or empty.", exception.getMessage());
    }

    @Test
    void createRecipe_noIngredients_throwsValidationException() {
        // Arrange
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Pasta");
        recipeDto.setIngredients(Set.of()); // Пустой список ингредиентов

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.createRecipe(recipeDto));
        assertEquals("Recipe must have at least one ingredient.", exception.getMessage());
    }

    @Test
    void createRecipe_ingredientWithEmptyName_throwsValidationException() {
        // Arrange
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Pasta");
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("  "); // Некорректное имя ингредиента
        recipeDto.setIngredients(Set.of(ingredientDto));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.createRecipe(recipeDto));
        assertEquals("Ingredient name cannot be null or empty.", exception.getMessage());
    }

    @Test
    void findRecipesByTitle_notFound_throwsNotFoundException() {
        // Arrange
        String title = "Unknown";
        String cacheKey = "recipes_by_title_" + title.toLowerCase();

        when(cacheService.containsKey(cacheKey)).thenReturn(false);
        when(recipeRepository.findByTitleContainingIgnoreCase(title)).thenReturn(List.of());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> recipeService.findRecipesByTitle(title));
        assertEquals("No recipes found with title containing: " + title, exception.getMessage());
    }

    @Test
    void findRecipesByTitle_emptyTitle_throwsValidationException() {
        // Arrange
        String title = "   "; // Пустое или состоящее из пробелов название

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByTitle(title));
        assertEquals("Recipe title cannot be null or empty.", exception.getMessage());
    }

    @Test
    void findRecipesByTitle_nullTitle_throwsValidationException() {
        // Arrange
        String title = null;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByTitle(title));
        assertEquals("Recipe title cannot be null or empty.", exception.getMessage());
    }

    @Test
    void deleteRecipeById_success() {
        // Arrange
        Long recipeId = 1L;
        Recipe recipe = new Recipe(recipeId, "Recipe", "Description", "Instruction");
        recipe.setIngredients(new HashSet<>());

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Act
        recipeService.deleteRecipeById(recipeId);

        // Assert
        verify(recipeRepository, times(1)).delete(recipe);
        verify(cacheService, times(1)).evict("recipe_" + recipeId);
    }

    @Test
    void deleteRecipeById_invalidId_throwsValidationException() {
        // Arrange
        Long recipeId = -1L;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.deleteRecipeById(recipeId));
        assertEquals("Recipe ID must be greater than 0.", exception.getMessage());
    }

    @Test
    void deleteRecipeById_notFound_throwsNotFoundException() {
        // Arrange
        Long recipeId = 100L;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> recipeService.deleteRecipeById(recipeId));
        assertEquals("Recipe not found with ID " + recipeId, exception.getMessage());
    }


    @Test
    void findRecipesByAverageRating_invalidRating_throwsValidationException() {
        // Arrange
        String invalidRating = "invalid";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> recipeService.findRecipesByAverageRating(invalidRating));
        assertEquals("Rating must be a valid numeric value.", exception.getMessage());
    }
    @Test
    void findRecipesByAverageRating_emptyRating_throwsValidationException() {
        // Arrange
        String rating = " ";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByAverageRating(rating));
        assertEquals("Rating cannot be null or empty.", exception.getMessage());
    }

    @Test
    void findRecipesByAverageRating_outOfRange_throwsValidationException() {
        // Arrange
        String rating = "11";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByAverageRating(rating));
        assertEquals("Rating must be between 0 and 10.", exception.getMessage());
    }

    @Test
    void updateRecipe_success() {
        // Arrange
        Long recipeId = 1L;
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Updated Recipe");
        recipeDto.setDescription("Updated Description");
        recipeDto.setInstruction("Updated Instruction");

        Recipe existingRecipe = new Recipe(recipeId, "Old Recipe", "Old Description", "Old Instruction");
        Recipe updatedRecipe = new Recipe(recipeId, "Updated Recipe", "Updated Description", "Updated Instruction");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(existingRecipe)).thenReturn(updatedRecipe);
        when(recipeMapper.convertToDto(updatedRecipe)).thenReturn(recipeDto);

        // Act
        RecipeDto result = recipeService.updateRecipe(recipeId, recipeDto);

        // Assert
        assertEquals("Updated Recipe", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, times(1)).save(existingRecipe);
        verify(cacheService, times(1)).evict("recipe_" + recipeId);
    }

    @Test
    void updateRecipe_notFound_throwsNotFoundException() {
        // Arrange
        Long recipeId = 2L;
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Updated Recipe");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> recipeService.updateRecipe(recipeId, recipeDto));
        assertEquals("Recipe not found with ID " + recipeId, exception.getMessage());
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_invalidId_throwsValidationException() {
        // Arrange
        Long recipeId = -1L;
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Updated Recipe");

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.updateRecipe(recipeId, recipeDto));
        assertEquals("Recipe ID must be greater than 0.", exception.getMessage());
    }
    @Test
    void updateRecipe_emptyTitle_throwsValidationException() {
        // Arrange
        Long recipeId = 1L;
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle(""); // Пустое название

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.updateRecipe(recipeId, recipeDto));
        assertEquals("Recipe title cannot be null or empty.", exception.getMessage());
    }

    @Test
    void findRecipesByIngredientNames_success() {
        // Arrange
        List<String> ingredientNames = List.of("Salt", "Sugar");
        String cacheKey = "recipes_by_ingredients_" + ingredientNames.toString();

        Recipe recipe = new Recipe(1L, "Pasta", "Description", "Instruction");
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Pasta");

        when(cacheService.containsKey(cacheKey)).thenReturn(false);
        when(recipeRepository.findRecipesByIngredientNames(anyList(), anyLong())).thenReturn(List.of(recipe));
        when(recipeMapper.convertToDto(recipe)).thenReturn(recipeDto);

        // Act
        List<RecipeDto> result = recipeService.findRecipesByIngredientNames(ingredientNames);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
        verify(cacheService, times(1)).put(cacheKey, result);
    }
    @Test
    void findRecipesByTitle_ignoreCase_success() {
        // Arrange
        String title = "PASTA";
        Recipe recipe = new Recipe(1L, "Pasta", "Description", "Instruction");
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Pasta");

        when(cacheService.containsKey(anyString())).thenReturn(false);
        when(recipeRepository.findByTitleContainingIgnoreCase(title)).thenReturn(List.of(recipe));
        when(recipeMapper.convertToDto(recipe)).thenReturn(recipeDto);

        // Act
        List<RecipeDto> result = recipeService.findRecipesByTitle(title);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
    }

    @Test
    void findRecipesByAverageRating_floatValue_success() {
        // Arrange
        String rating = "4.5";
        Recipe recipe = new Recipe(1L, "Pasta", "Description", "Instruction");
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Pasta");

        when(cacheService.containsKey(anyString())).thenReturn(false);
        when(recipeRepository.findRecipesByAverageRating(anyDouble())).thenReturn(List.of(recipe));
        when(recipeMapper.convertToDto(recipe)).thenReturn(recipeDto);

        // Act
        List<RecipeDto> result = recipeService.findRecipesByAverageRating(rating);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
    }

    @Test
    void findRecipesByTitle_multipleMatches_success() {
        // Arrange
        String title = "Cake";
        Recipe recipe1 = new Recipe(1L, "Cake", "Chocolate Cake", "Bake it");
        Recipe recipe2 = new Recipe(2L, "Cake", "Vanilla Cake", "Mix and bake");
        RecipeDto recipeDto1 = new RecipeDto();
        recipeDto1.setTitle("Cake");
        RecipeDto recipeDto2 = new RecipeDto();
        recipeDto2.setTitle("Cake");

        when(cacheService.containsKey(anyString())).thenReturn(false);
        when(recipeRepository.findByTitleContainingIgnoreCase(title)).thenReturn(List.of(recipe1, recipe2));
        when(recipeMapper.convertToDto(recipe1)).thenReturn(recipeDto1);
        when(recipeMapper.convertToDto(recipe2)).thenReturn(recipeDto2);

        // Act
        List<RecipeDto> result = recipeService.findRecipesByTitle(title);

        // Assert
        assertEquals(2, result.size());
        verify(cacheService, times(1)).put(anyString(), anyList());
    }

    @Test
    void createRecipe_duplicateIngredients_success() {
        // Arrange
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("New Recipe");

        // Повторяющиеся ингредиенты
        IngredientDto ingredient1 = new IngredientDto();
        ingredient1.setName("Salt");
        IngredientDto ingredient2 = new IngredientDto();
        ingredient2.setName("Salt"); // Повторяющийся ингредиент
        recipeDto.setIngredients(new HashSet<>(Set.of(ingredient1, ingredient2))); // Уникальность обеспечивается Set

        Recipe recipe = new Recipe("New Recipe", "Description", "Instruction");
        Ingredient ingredient = new Ingredient("Salt");

        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.of(ingredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(recipeMapper.convertToDto(any(Recipe.class))).thenReturn(recipeDto);

        // Act
        RecipeDto result = recipeService.createRecipe(recipeDto);

        // Assert
        assertEquals("New Recipe", result.getTitle());
        verify(ingredientRepository, times(1)).findByName("Salt"); // Проверяем, что вызов был только 1 раз
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }


    @Test
    void findRecipesByIngredientNames_notFound_throwsNotFoundException() {
        // Arrange
        List<String> ingredientNames = List.of("Unicorn Spice");
        String cacheKey = "recipes_by_ingredients_" + ingredientNames.toString();

        when(cacheService.containsKey(cacheKey)).thenReturn(false);
        when(recipeRepository.findRecipesByIngredientNames(anyList(), anyLong())).thenReturn(List.of());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> recipeService.findRecipesByIngredientNames(ingredientNames));
        assertEquals("No recipes found with ingredients: [Unicorn Spice]", exception.getMessage());
    }
    @Test
    void findRecipesByAverageRating_maxValue_success() {
        // Arrange
        String rating = "10";
        Recipe recipe = new Recipe(1L, "Recipe 10", "Description", "Instruction");
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setTitle("Recipe 10");

        when(cacheService.containsKey(anyString())).thenReturn(false);
        when(recipeRepository.findRecipesByAverageRating(10.0)).thenReturn(List.of(recipe));
        when(recipeMapper.convertToDto(recipe)).thenReturn(recipeDto);

        // Act
        List<RecipeDto> result = recipeService.findRecipesByAverageRating(rating);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Recipe 10", result.get(0).getTitle());
    }

    @Test
    void findRecipesByAverageRating_invalidNumericRating_throwsValidationException() {
        // Arrange
        String rating = "abc";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByAverageRating(rating));
        assertEquals("Rating must be a valid numeric value.", exception.getMessage());
    }

    @Test
    void findRecipesByAverageRating_nullRating_throwsValidationException() {
        // Arrange
        String rating = null;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByAverageRating(rating));
        assertEquals("Rating cannot be null or empty.", exception.getMessage());
    }

    @Test
    void findRecipesByAverageRating_noRecipesFound_throwsNotFoundException() {
        // Arrange
        String rating = "5";
        double normalizedRating = 5.0;

        when(recipeRepository.findRecipesByAverageRating(normalizedRating)).thenReturn(Collections.emptyList());
        when(cacheService.containsKey("recipes_by_rating_" + normalizedRating)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> recipeService.findRecipesByAverageRating(rating));
        assertEquals("No recipes found with average rating: " + normalizedRating, exception.getMessage());
    }
    @Test
    void findRecipesByAverageRating_ratingOutOfRange_throwsValidationException() {
        // Arrange
        String rating = "-3";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByAverageRating(rating));
        assertEquals("Rating must be between 0 and 10.", exception.getMessage());
    }

    @Test
    void findRecipesByIngredientNames_emptyList_throwsValidationException() {
        // Arrange
        List<String> ingredientNames = Collections.emptyList();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByIngredientNames(ingredientNames));
        assertEquals("Ingredient list cannot be null or empty.", exception.getMessage());
    }
    @Test
    void findRecipesByIngredientNames_nullList_throwsValidationException() {
        // Arrange
        List<String> ingredientNames = null;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> recipeService.findRecipesByIngredientNames(ingredientNames));
        assertEquals("Ingredient list cannot be null or empty.", exception.getMessage());
    }


}