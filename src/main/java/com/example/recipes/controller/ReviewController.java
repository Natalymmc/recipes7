package com.example.recipes.controller;

import com.example.recipes.dto.ReviewDto;
import com.example.recipes.service.ReviewService;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
public class ReviewController {

    private final ReviewService reviewService;


    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{recipeId}/reviews")
    public ResponseEntity<ReviewDto> addReviewToRecipe(
            @PathVariable Long recipeId,
            @RequestBody ReviewDto reviewDto) {
        ReviewDto addedReview = reviewService.addReviewToRecipe(recipeId, reviewDto);
        return ResponseEntity.ok(addedReview);
    }

    @DeleteMapping("/{recipeId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReviewFromRecipe(
            @PathVariable Long recipeId,
            @PathVariable Long reviewId) {
        reviewService.deleteReviewFromRecipe(recipeId, reviewId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{recipeId}/reviews/{reviewId}")
    public ResponseEntity<ReviewDto> updateReviewForRecipe(
            @PathVariable Long recipeId,
            @PathVariable Long reviewId,
            @RequestBody ReviewDto reviewDto) {
        ReviewDto updatedReview = reviewService
                .updateReviewForRecipe(recipeId, reviewId, reviewDto);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/{recipeId}/reviews")
    public ResponseEntity<Set<ReviewDto>> getAllReviewsForRecipe(@PathVariable Long recipeId) {
        Set<ReviewDto> reviews = reviewService.getAllReviewsForRecipe(recipeId);
        return ResponseEntity.ok(reviews);
    }
}
