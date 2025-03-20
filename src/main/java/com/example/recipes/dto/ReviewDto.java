package com.example.recipes.dto;

public class ReviewDto {
    private String message;
    private Integer rating;

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public Integer getRating() {

        return rating;
    }

    public void setRating(Integer rating) {

        this.rating = rating;
    }
}