package com.example.recipes.service;

import com.example.recipes.dto.IngredientDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.exceptions.NotFoundException;
import com.example.recipes.exceptions.ValidationException;
import com.example.recipes.mapper.IngredientMapper;
import com.example.recipes.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @Test
    void deleteIngredient_validId_success() {
        // Arrange
        Long ingredientId = 1L;
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        ingredient.setRecipes(new HashSet<>());

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

        // Act
        ingredientService.deleteIngredient(ingredientId);

        // Assert
        verify(ingredientRepository, times(1)).delete(ingredient);
    }

    @Test
    void deleteIngredient_invalidId_throwsValidationException() {
        // Arrange
        Long invalidId = -1L;

        // Act & Assert
        assertThrows(ValidationException.class, () -> ingredientService.deleteIngredient(invalidId));
        verifyNoInteractions(ingredientRepository);
    }

    @Test
    void deleteIngredient_notFound_throwsNotFoundException() {
        // Arrange
        Long ingredientId = 2L;

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> ingredientService.deleteIngredient(ingredientId));
        verify(ingredientRepository, times(1)).findById(ingredientId);
    }

    @Test
    void deleteIngredient_associatedWithRecipes_success() {
        // Arrange
        Long ingredientId = 1L;
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        ingredient.setRecipes(new HashSet<>(Arrays.asList(recipe1, recipe2)));
        recipe1.getIngredients().add(ingredient);
        recipe2.getIngredients().add(ingredient);

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

        // Act
        ingredientService.deleteIngredient(ingredientId);

        // Assert
        assertFalse(recipe1.getIngredients().contains(ingredient));
        assertFalse(recipe2.getIngredients().contains(ingredient));
        verify(ingredientRepository, times(1)).delete(ingredient);
    }


    @Test
    void createIngredient_validData_success() {
        // Arrange
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Sugar");

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setId(1L);
        savedIngredient.setName("Sugar");

        when(ingredientRepository.findByName("Sugar")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);

        // Act
        IngredientDto result = ingredientService.createIngredient(ingredientDto);

        // Assert
        assertEquals(savedIngredient.getId(), result.getId());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void createIngredient_emptyName_throwsValidationException() {
        // Arrange
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("  "); // Invalid name

        // Act & Assert
        assertThrows(ValidationException.class, () -> ingredientService.createIngredient(ingredientDto));
        verifyNoInteractions(ingredientRepository);
    }

    @Test
    void createIngredient_duplicateName_throwsValidationException() {
        // Arrange
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Salt");

        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.of(new Ingredient()));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ingredientService.createIngredient(ingredientDto));
        verify(ingredientRepository, times(1)).findByName("Salt");
    }

    @Test
    void createIngredient_nameWithSpaces_success() {
        // Arrange
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("  Sugar  ");

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setId(1L);
        savedIngredient.setName("Sugar");

        when(ingredientRepository.findByName("Sugar")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);

        // Act
        IngredientDto result = ingredientService.createIngredient(ingredientDto);

        // Assert
        assertEquals("Sugar", result.getName());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }


    @Test
    void updateIngredient_validData_success() {
        // Arrange
        Long ingredientId = 1L;
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Flour");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        ingredient.setName("Sugar");

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setId(ingredientId);
        savedIngredient.setName("Flour");

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);

        // Act
        IngredientDto result = ingredientService.updateIngredient(ingredientId, ingredientDto);

        // Assert
        assertEquals("Flour", result.getName());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }


    @Test
    void updateIngredient_invalidId_throwsValidationException() {
        // Arrange
        Long invalidId = -1L;
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Salt");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ingredientService.updateIngredient(invalidId, ingredientDto));
        verifyNoInteractions(ingredientRepository);
    }

    @Test
    void updateIngredient_notFound_throwsNotFoundException() {
        // Arrange
        Long ingredientId = 2L;
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Salt");

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> ingredientService.updateIngredient(ingredientId, ingredientDto));
        verify(ingredientRepository, times(1)).findById(ingredientId);
    }

    @Test
    void updateIngredient_duplicateName_throwsValidationException() {
        // Arrange
        Long ingredientId = 1L;
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Salt"); // Имя, которое уже существует

        Ingredient existingIngredient = new Ingredient();
        existingIngredient.setId(ingredientId);
        existingIngredient.setName("Sugar");

        Ingredient duplicateIngredient = new Ingredient();
        duplicateIngredient.setId(2L);
        duplicateIngredient.setName("Salt");

        // Заглушка для поиска существующего ингредиента по ID
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(existingIngredient));

        // Заглушка для проверки наличия ингредиента с таким же именем
        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.of(duplicateIngredient));

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> ingredientService.updateIngredient(ingredientId, ingredientDto)
        );

        // Проверяем, что выбрасывается корректное сообщение об ошибке
        assertEquals("Ingredient with name 'Salt' already exists.", exception.getMessage());

        verify(ingredientRepository, times(1)).findById(ingredientId);
        verify(ingredientRepository, times(1)).findByName("Salt");
        verifyNoMoreInteractions(ingredientRepository);
    }

    @Test
    void updateIngredient_emptyName_throwsValidationException() {
        Long ingredientId = 2L;
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName(" ");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ingredientService.updateIngredient(ingredientId, ingredientDto));
        assertEquals("Ingredient name cannot be null or empty.", exception.getMessage());
    }

    @Test
    void updateIngredient_nullName_throwsValidationException() {
        Long ingredientId = 2L;
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ingredientService.updateIngredient(ingredientId, ingredientDto));
        assertEquals("Ingredient name cannot be null or empty.", exception.getMessage());
    }

    @Test
    void updateIngredient_sameName_success() {
        // Arrange
        Long ingredientId = 1L;
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Sugar");

        Ingredient existingIngredient = new Ingredient();
        existingIngredient.setId(ingredientId);
        existingIngredient.setName("Sugar");

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IngredientDto result = ingredientService.updateIngredient(ingredientId, ingredientDto);

        // Assert
        assertEquals("Sugar", result.getName());
        verify(ingredientRepository).save(existingIngredient);
    }

}
