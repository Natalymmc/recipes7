package com.example.recipes.controller;

import com.example.recipes.dto.ReviewDto;
import com.example.recipes.entity.Review;
import com.example.recipes.mapper.ReviewMapper;
import com.example.recipes.service.ReviewService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes/{recipeId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping
    public Review createReview(@PathVariable Long recipeId, @RequestBody Review review) {
        return reviewService.createReview(recipeId, review);
    }

    // возможно поменять пар-ры и соотв и ф-цию
    @PutMapping("/{reviewId}")
    public Review updateReview(@PathVariable Integer reviewId, @RequestBody Review review) {
        return reviewService.updateReview(reviewId, review);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/all")
    public List<Review> findAllReviews() {
        return reviewService.findAllReviews();
    }

    @GetMapping
    public List<ReviewDto> getReviewsByRecipeId(@PathVariable Long recipeId) {
        List<Review> reviews = reviewService.getReviewsByRecipeId(recipeId);
        return reviews.stream()
                .map(reviewMapper::toDto)
                .toList();
    }
}
