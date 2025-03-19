package com.example.recipes.service;

import com.example.recipes.entity.Recipe;
import com.example.recipes.entity.Review;
import com.example.recipes.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RecipeService recipeService;

    public ReviewService(ReviewRepository reviewRepository, RecipeService bookService) {
        this.reviewRepository = reviewRepository;
        this.recipeService = bookService;
    }

    public Review createReview(Long recipeId, Review review) {
        Recipe recipe = recipeService.findById(recipeId);
        if (recipe == null) {
            throw new EntityNotFoundException("Recipe not found");
        }

        review.setRecipe(recipe);
        return reviewRepository.save(review);
    }

    public Review updateReview(Integer id, Review review) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Recipe not found");
        }
        Review initialReview = reviewRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Review not found"));
        initialReview.setMessage(review.getMessage());
        return reviewRepository.save(initialReview);
    }

    public void deleteReview(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(reviewId);

    }

    public List<Review> getReviewsByRecipeId(Long recipeId) {

        return reviewRepository.findByRecipeId(recipeId);
    }

    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }
}