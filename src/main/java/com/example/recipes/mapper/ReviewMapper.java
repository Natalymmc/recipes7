package com.example.recipes.mapper;

import com.example.recipes.dto.ReviewDto;
import com.example.recipes.entity.Review;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public Set<ReviewDto> convertToDto(Set<Review> reviews) {
        if (reviews == null) {
            return new HashSet<>();
        }
        return reviews.stream().map(review -> {
            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setId(review.getId());
            reviewDto.setMessage(review.getMessage());
            reviewDto.setRating(review.getRating());
            return reviewDto;
        }).collect(Collectors.toSet());
    }
}

