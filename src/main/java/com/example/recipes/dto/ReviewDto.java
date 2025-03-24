package com.example.recipes.dto;

public class ReviewDto {
    private Long id;
    private String message;
    private Integer rating;

    public ReviewDto() {

    }

    public ReviewDto(Long id, String message, Integer rating) {
        this.id = id;
        this.message = message;
        this.rating = rating;
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}