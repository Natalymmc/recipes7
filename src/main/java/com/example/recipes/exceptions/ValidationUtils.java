package com.example.recipes.exceptions;

import com.example.recipes.dto.ReviewDto;
import java.util.ArrayList;
import java.util.List;

public class ValidationUtils {
    public static List<String> validateReviewInput(Long recipeId, ReviewDto reviewDto) {
        List<String> errors = new ArrayList<>();

        if (recipeId == null || recipeId <= 0) {
            errors.add("Recipe ID must be greater than 0.");
        }
        if (reviewDto.getMessage() == null || reviewDto.getMessage().trim().isEmpty()) {
            errors.add("Review message cannot be null or empty.");
        }
        if (reviewDto.getRating() == null || reviewDto.getRating() < 0
                || reviewDto.getRating() > 10) {
            errors.add("Review rating must be between 0 and 10.");
        }
        if (reviewDto.getMessage().length() > 1000) {
            errors.add("Review message length cannot exceed 1000 characters.");
        }


        return errors;
    }
}
