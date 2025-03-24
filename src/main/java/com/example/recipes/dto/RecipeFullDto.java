package com.example.recipes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class RecipeFullDto {
    private Long id;
    private String title;
    private String description;
    private String instruction;
    private Set<IngredientDto> ingredients;
    private Set<ReviewDto> reviews;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setIngredients(Set<IngredientDto> ingredients) {
        this.ingredients = ingredients;
    }

    public void setReviews(Set<ReviewDto> reviews) {
        this.reviews = reviews;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getInstruction() {
        return instruction;
    }

    public Set<IngredientDto> getIngredients() {
        return ingredients;
    }

    public Set<ReviewDto> getReviews() {
        return reviews;
    }

}

