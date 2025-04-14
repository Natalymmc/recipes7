package com.example.recipes.controller;

import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.RecipeFullDto;
import com.example.recipes.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Управление рецептами", description = "API для операций с рецептами")
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(
            summary = "Добавить список рецептов",
            description = "Добавляет сразу несколько рецептов.",
            responses = {
                @ApiResponse(responseCode = "200",
                            description = "Список рецептов успешно добавлен."),
                @ApiResponse(responseCode = "400",
                            description = "Ошибка валидации данных рецептов.")
            }
    )
    @PostMapping("/bulk")
    public ResponseEntity<List<RecipeDto>> addRecipes(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список рецептов для добавления",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RecipeDto[].class))
            )
            @RequestBody List<RecipeDto> recipeDtos) {

        // Используем Stream API для обработки списка
        List<RecipeDto> addedRecipes = recipeDtos.stream()
                .map(recipeService::createRecipe)
                .toList();

        return ResponseEntity.ok(addedRecipes);
    }

    @Operation(
            summary = "Получить все рецепты",
            description = "Возвращает полный список всех рецептов.",
            responses = {
                @ApiResponse(responseCode = "200",
                            description = "Список рецептов успешно получен.")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<RecipeFullDto>> getAllRecipes() {
        List<RecipeFullDto> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @Operation(
            summary = "Получить рецепт по ID",
            description = "Возвращает рецепт по указанному уникальному идентификатору.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Рецепт найден."),
                @ApiResponse(responseCode = "404", description = "Рецепт с указанным ID не найден.")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @Operation(
            summary = "Найти рецепты по названию",
            description = "Ищет рецепты, которые содержат указанное название.",
            responses = {
                @ApiResponse(responseCode = "200",
                            description = "Список рецептов успешно найден."),
                @ApiResponse(responseCode = "404",
                            description = "Рецепты с указанным названием не найдены.")
            }
    )
    @GetMapping
    public ResponseEntity<List<RecipeDto>> getRecipesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(recipeService.findRecipesByTitle(title));
    }

    @Operation(
            summary = "Создать рецепт",
            description = "Создаёт новый рецепт на основе предоставленных данных.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Рецепт успешно создан."),
                @ApiResponse(responseCode = "400",
                        description = "Некорректные данные для создания рецепта.")
            }
    )
    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.createRecipe(recipeDto));
    }

    @Operation(
            summary = "Обновить рецепт",
            description = "Обновляет существующий рецепт по указанному ID.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Рецепт успешно обновлён."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепт с указанным ID не найден."),
                @ApiResponse(responseCode = "400",
                        description = "Некорректные данные для обновления рецепта.")
            }
    )
    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> updateRecipe(@PathVariable Long recipeId,
                                                      @RequestBody RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId, recipeDto));
    }

    @Operation(
            summary = "Удалить рецепт",
            description = "Удаляет рецепт по его уникальному идентификатору.",
            responses = {
                @ApiResponse(responseCode = "204",
                        description = "Рецепт успешно удалён."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепт с указанным ID не найден.")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Найти рецепты по ингредиентам",
            description = "Ищет рецепты, содержащие указанные ингредиенты.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Список рецептов успешно найден."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепты с указанными ингредиентами не найдены.")
            }
    )
    @GetMapping("/search/multiple-ingredients")
    public ResponseEntity<List<RecipeDto>> findRecipesByIngredientNames(
            @RequestParam List<String> ingredientNames) {
        List<RecipeDto> recipes = recipeService.findRecipesByIngredientNames(ingredientNames);
        return ResponseEntity.ok(recipes);
    }

    @Operation(
            summary = "Найти рецепты по рейтингу",
            description = "Ищет рецепты с указанным средним рейтингом.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Список рецептов успешно найден."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепты с указанным рейтингом не найдены.")
            }
    )
    @GetMapping("/search/average-rating")
    public ResponseEntity<List<RecipeDto>> findRecipesByAverageRating(@RequestParam String rating) {
        List<RecipeDto> recipes = recipeService.findRecipesByAverageRating(rating);
        return ResponseEntity.ok(recipes);
    }
}