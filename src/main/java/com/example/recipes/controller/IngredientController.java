package com.example.recipes.controller;

import com.example.recipes.dto.IngredientDto;
import com.example.recipes.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Запросы ингредментов", description = "CRUD operations for ingredients")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/all")
    public List<IngredientDto> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @Operation(
            summary = "Delete ingredient",
            description = "Deletes an ingredient by its ID.",
            responses = {
                @ApiResponse(responseCode = "204",
                        description = "Ингредиент успешно удалён."),
                @ApiResponse(responseCode = "404",
                        description = "Ингредиент с указанным ID не найден.")
            }
    )
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @Parameter(description = "ID ингредиента, который нужно удалить", required = true)
            @PathVariable Long ingredientId) {
        ingredientService.deleteIngredient(ingredientId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Создать новый ингредиент",
            description = "Создаёт новый ингредиент на основе предоставленных данных.",
            responses = {
                @ApiResponse(responseCode = "200",
                            description = "Ингредиент успешно создан."),
                @ApiResponse(responseCode = "400",
                            description = "Ошибка валидации данных.")
            }
    )
    @PostMapping
   public ResponseEntity<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ResponseEntity.ok(ingredientService.createIngredient(ingredientDto));
    }

    @Operation(
            summary = "Обновить ингредиент",
            description = "Обновляет информацию об ингредиенте на основе его ID.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Ингредиент успешно обновлён."),
                @ApiResponse(responseCode = "404",
                        description = "Ингредиент с указанным ID не найден."),
                @ApiResponse(responseCode = "400",
                        description = "Ошибка валидации данных.")
            }
    )
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientDto> updateIngredient(
            @Parameter(description = "ID ингредиента, который нужно обновить", required = true)
            @PathVariable Long ingredientId,
            @RequestBody IngredientDto ingredientDto) {
        IngredientDto updatedIngredient = ingredientService
                .updateIngredient(ingredientId, ingredientDto);
        return ResponseEntity.ok(updatedIngredient);
    }
}


