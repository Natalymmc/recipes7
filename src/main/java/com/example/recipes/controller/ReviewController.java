package com.example.recipes.controller;

import com.example.recipes.dto.ReviewDto;
import com.example.recipes.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Управление отзывами", description = "API для операций с отзывами к рецептам")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/recipes")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(
            summary = "Добавить список отзывов к рецепту",
            description = "Добавляет сразу несколько отзывов для указанного рецепта.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Список отзывов успешно добавлен."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепт с указанным ID не найден."),
                @ApiResponse(responseCode = "400",
                        description = "Ошибка валидации данных отзывов.")
            }
    )
    @PostMapping("/{recipeId}/reviews/bulk")
    public ResponseEntity<List<ReviewDto>> addReviewsToRecipe(
           @Parameter(description = "ID рецепта, к которому нужно добавить отзывы", required = true)
            @PathVariable Long recipeId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список отзывов для добавления",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReviewDto[].class))
            )
            @RequestBody List<ReviewDto> reviewDtos) {

        List<ReviewDto> addedReviews = reviewDtos.stream()
                .map(reviewDto -> reviewService.addReviewToRecipe(recipeId, reviewDto))
                .toList();

        return ResponseEntity.ok(addedReviews);
    }

    @Operation(
            summary = "Добавить отзыв к рецепту",
            description = "Добавляет новый отзыв к указанному рецепту.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Отзыв успешно добавлен."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепт с указанным ID не найден."),
                @ApiResponse(responseCode = "400",
                        description = "Ошибка валидации данных отзыва.")
            }
    )
    @PostMapping("/{recipeId}/reviews")
    public ResponseEntity<ReviewDto> addReviewToRecipe(
            @PathVariable Long recipeId,
            @RequestBody ReviewDto reviewDto) {
        ReviewDto addedReview = reviewService.addReviewToRecipe(recipeId, reviewDto);
        return ResponseEntity.ok(addedReview);
    }

    @Operation(
            summary = "Удалить отзыв из рецепта",
            description = "Удаляет отзыв из указанного рецепта.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Отзыв успешно удалён."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепт или отзыв с указанным ID не найден.")
            }
    )
    @DeleteMapping("/{recipeId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReviewFromRecipe(
            @PathVariable Long recipeId,
            @PathVariable Long reviewId) {
        reviewService.deleteReviewFromRecipe(recipeId, reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Обновить отзыв к рецепту",
            description = "Обновляет существующий отзыв к указанному рецепту.",
            responses = {
                @ApiResponse(responseCode = "200",
                        description = "Отзыв успешно обновлён."),
                @ApiResponse(responseCode = "404",
                        description = "Рецепт или отзыв с указанным ID не найден."),
                @ApiResponse(responseCode = "400",
                        description = "Ошибка валидации данных отзыва.")
            }
    )
    @PutMapping("/{recipeId}/reviews/{reviewId}")
    public ResponseEntity<ReviewDto> updateReviewForRecipe(
            @PathVariable Long recipeId,
            @PathVariable Long reviewId,
            @RequestBody ReviewDto reviewDto) {
        ReviewDto updatedReview = reviewService
                .updateReviewForRecipe(recipeId, reviewId, reviewDto);
        return ResponseEntity.ok(updatedReview);
    }

    @Operation(
            summary = "Получить все отзывы для рецепта",
            description = "Возвращает список всех отзывов для указанного рецепта.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Список отзывов успешно получен."),
                @ApiResponse(responseCode = "404", description = "Рецепт с указанным ID не найден.")
            }
    )
    @GetMapping("/{recipeId}/reviews")
    public ResponseEntity<Set<ReviewDto>> getAllReviewsForRecipe(@PathVariable Long recipeId) {
        Set<ReviewDto> reviews = reviewService.getAllReviewsForRecipe(recipeId);
        return ResponseEntity.ok(reviews);
    }
}
