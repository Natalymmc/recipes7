package com.example.recipes.mapper;

import com.example.recipes.dto.ReviewDto;
import com.example.recipes.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto toDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setMessage(review.getMessage());
        reviewDto.setRating(review.getRating());
        return reviewDto;
    }

    public Review toEntity(ReviewDto reviewDto) {
        Review review = new Review();
        review.setMessage(reviewDto.getMessage());
        review.setRating(reviewDto.getRating());
        return review;
    }
}