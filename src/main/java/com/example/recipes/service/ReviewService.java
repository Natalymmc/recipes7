package com.example.recipes.service;

import com.example.recipes.dto.ReviewDto;
import com.example.recipes.entity.Recipe;
import com.example.recipes.entity.Review;
import com.example.recipes.repository.RecipeRepository;
import com.example.recipes.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;


    public ReviewService(ReviewRepository reviewRepository, RecipeRepository recipeRepository) {
        this.reviewRepository = reviewRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public ReviewDto addReviewToRecipe(Long recipeId, ReviewDto reviewDto) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new EntityNotFoundException("Recipe not found with id " + recipeId));

        Review review = new Review();
        review.setMessage(reviewDto.getMessage());
        review.setRating(reviewDto.getRating());
        review.setRecipe(recipe);

        Review savedReview = reviewRepository.save(review);
        return new ReviewDto(savedReview.getId(),
                savedReview.getMessage(), savedReview.getRating());
    }

    public void deleteReviewFromRecipe(Long recipeId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new EntityNotFoundException("Review not found with id " + reviewId));

        if (!review.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("Review does not belong to the specified recipe");
        }

        reviewRepository.delete(review);
    }

    public ReviewDto updateReviewForRecipe(Long recipeId, Long reviewId, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new EntityNotFoundException("Review not found with id " + reviewId));

        if (!review.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("Review does not belong to the specified recipe");
        }

        review.setMessage(reviewDto.getMessage());
        review.setRating(reviewDto.getRating());
        Review updatedReview = reviewRepository.save(review);

        return new ReviewDto(updatedReview.getId(),
                updatedReview.getMessage(), updatedReview.getRating());
    }

    @Transactional(readOnly = true)
    public Set<ReviewDto> getAllReviewsForRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new EntityNotFoundException("Recipe not found with id " + recipeId));

        return recipe.getReviews().stream().map(review ->
                new ReviewDto(review.getId(), review.getMessage(), review.getRating())
        ).collect(Collectors.toSet());
    }

}

