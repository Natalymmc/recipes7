package com.example.recipes.service;

import com.example.recipes.dto.ReviewDto;
import com.example.recipes.entity.Recipe;
import com.example.recipes.entity.Review;
import com.example.recipes.exceptions.NotFoundException;
import com.example.recipes.exceptions.ValidationException;
import com.example.recipes.repository.RecipeRepository;
import com.example.recipes.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    void deleteReviewFromRecipe_success() {
        Long recipeId = 1L;
        Long reviewId = 10L;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Review review = new Review();
        review.setId(reviewId);
        review.setRecipe(recipe);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReviewFromRecipe(recipeId, reviewId);

        verify(reviewRepository, times(1)).delete(review);
    }
    @Test
    void addReviewToRecipe_success() {

        Long recipeId = 1L;
        ReviewDto reviewDto = new ReviewDto(null, "Great recipe!", 5);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Review review = new Review();
        review.setMessage("Great recipe!");
        review.setRating(5);
        review.setRecipe(recipe);

        Review savedReview = new Review(1L, "Great recipe!", 5, recipe);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewDto result = reviewService.addReviewToRecipe(recipeId, reviewDto);


        assertNotNull(result);
        assertEquals("Great recipe!", result.getMessage());
        assertEquals(5, result.getRating());
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void updateReviewForRecipe_success() {

        Long recipeId = 1L;
        Long reviewId = 10L;
        ReviewDto reviewDto = new ReviewDto(null, "Updated review", 4);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Review review = new Review();
        review.setId(reviewId);
        review.setMessage("Original review");
        review.setRating(5);
        review.setRecipe(recipe);

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setMessage("Updated review");
        updatedReview.setRating(4);
        updatedReview.setRecipe(recipe);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        ReviewDto result = reviewService.updateReviewForRecipe(recipeId, reviewId, reviewDto);

        // Assert
        assertEquals("Updated review", result.getMessage());
        assertEquals(4, result.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReviewToRecipe_invalidRating_throwsValidationException() {
        Long recipeId = 1L;
        ReviewDto reviewDto = new ReviewDto(null, "Amazing!", 11); // Рейтинг вне диапазона

        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.addReviewToRecipe(recipeId, reviewDto));
        assertTrue(exception.getMessage().contains("Review rating must be between 0 and 10."));
        verifyNoInteractions(recipeRepository);
        verifyNoInteractions(reviewRepository);
    }
    @Test
    void addReviewToRecipe_emptyMessage_throwsValidationException() {
        Long recipeId = 1L;
        ReviewDto reviewDto = new ReviewDto(null, "   ", 5); // Пустое сообщение

        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.addReviewToRecipe(recipeId, reviewDto));
        assertTrue(exception.getMessage().contains("Review message cannot be null or empty."));
        verifyNoInteractions(recipeRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void addReviewToRecipe_recipeNotFound_throwsNotFoundException() {
        // Arrange
        Long recipeId = 1L;
        ReviewDto reviewDto = new ReviewDto(null, "Amazing!", 5);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> reviewService.addReviewToRecipe(recipeId, reviewDto));
        assertEquals("Recipe not found with id " + recipeId, exception.getMessage());
        verify(recipeRepository, times(1)).findById(recipeId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void addMultipleReviews_success() {
        Long recipeId = 1L;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        ReviewDto reviewDto1 = new ReviewDto(null, "Amazing recipe!", 5);
        ReviewDto reviewDto2 = new ReviewDto(null, "Needs more salt", 3);

        Review savedReview1 = new Review(1L, "Amazing recipe!", 5, recipe);
        Review savedReview2 = new Review(2L, "Needs more salt", 3, recipe);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(reviewRepository.save(any(Review.class)))
                .thenReturn(savedReview1)
                .thenReturn(savedReview2);

        ReviewDto result1 = reviewService.addReviewToRecipe(recipeId, reviewDto1);
        ReviewDto result2 = reviewService.addReviewToRecipe(recipeId, reviewDto2);

        assertEquals("Amazing recipe!", result1.getMessage());
        assertEquals("Needs more salt", result2.getMessage());
        verify(reviewRepository, times(2)).save(any(Review.class));
    }

    @Test
    void deleteReviewFromRecipe_reviewNotFound_throwsNotFoundException() {
        Long recipeId = 1L;
        Long reviewId = 10L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> reviewService.deleteReviewFromRecipe(recipeId, reviewId));
        assertEquals("Review not found with id " + reviewId, exception.getMessage());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void deleteReviewFromRecipe_invalidRecipeId_throwsValidationException() {
        // Arrange
        Long recipeId = 0L;
        Long reviewId = 10L;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.deleteReviewFromRecipe(recipeId, reviewId));
        assertEquals("Recipe ID must be greater than 0.", exception.getMessage());
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void deleteReviewFromRecipe_invalidReviewId_throwsValidationException() {
        // Arrange
        Long recipeId = 1L;
        Long reviewId = 0L;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.deleteReviewFromRecipe(recipeId, reviewId));
        assertEquals("Review ID must be greater than 0.", exception.getMessage());
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void deleteReviewFromRecipe_nullRecipeId_throwsValidationException() {
        // Arrange
        Long recipeId = null;
        Long reviewId = 10L;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.deleteReviewFromRecipe(recipeId, reviewId));
        assertEquals("Recipe ID must be greater than 0.", exception.getMessage());
    }


    @Test
    void updateReviewForRecipe_reviewNotBelongToRecipe_throwsIllegalArgumentException() {
        // Arrange
        Long recipeId = 1L;
        Long reviewId = 10L;
        ReviewDto reviewDto = new ReviewDto(null, "Updated message", 4);

        Recipe anotherRecipe = new Recipe();
        anotherRecipe.setId(2L);

        Review review = new Review();
        review.setId(reviewId);
        review.setRecipe(anotherRecipe);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.updateReviewForRecipe(recipeId, reviewId, reviewDto));
        assertEquals("Review does not belong to the specified recipe", exception.getMessage());
    }

    @Test
    void getAllReviewsForRecipe_success() {
        // Arrange
        Long recipeId = 1L;

        Review review1 = new Review(1L, "Great!", 5, null);
        Review review2 = new Review(2L, "Not bad", 4, null);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setReviews(new HashSet<>(Set.of(review1, review2)));

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Act
        Set<ReviewDto> result = reviewService.getAllReviewsForRecipe(recipeId);

        // Assert
        assertEquals(2, result.size());
        verify(recipeRepository, times(1)).findById(recipeId);
    }
    @Test
    void getAllReviewsForRecipe_recipeNotFound_throwsNotFoundException() {
        // Arrange
        Long recipeId = 1L;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> reviewService.getAllReviewsForRecipe(recipeId));
        assertEquals("Recipe not found with id " + recipeId, exception.getMessage());
    }
    @Test
    void getAllReviewsForRecipe_invalidRecipeId_throwsValidationException() {
        // Arrange
        Long recipeId = 0L;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.getAllReviewsForRecipe(recipeId));
        assertEquals("Recipe ID must be greater than 0.", exception.getMessage());
        verifyNoInteractions(recipeRepository);
    }
    @Test
    void updateReviewForRecipe_reviewNotFound_throwsNotFoundException() {
        // Arrange
        Long recipeId = 1L;
        Long reviewId = 10L;
        ReviewDto reviewDto = new ReviewDto(null, "Updated message", 5);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> reviewService.updateReviewForRecipe(recipeId, reviewId, reviewDto));
        assertEquals("Review not found with id " + reviewId, exception.getMessage());
    }
    @Test
    void getAllReviewsForRecipe_noReviews_throwsValidationException() {
        // Arrange
        Long recipeId = 1L;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setReviews(Collections.emptySet()); // Пустой список отзывов

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.getAllReviewsForRecipe(recipeId));
        assertEquals("No reviews found for recipe ID " + recipeId, exception.getMessage());
    }


    @Test
    void addReviewToRecipe_messageTooLong_throwsValidationException() {
        // Arrange
        Long recipeId = 1L;
        String longMessage = "A".repeat(1001); // Сообщение длиной более 1000 символов
        ReviewDto reviewDto = new ReviewDto(null, longMessage, 5);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> reviewService.addReviewToRecipe(recipeId, reviewDto));
        assertTrue(exception.getMessage().contains("Review message length cannot exceed 1000 characters."));
    }

}
