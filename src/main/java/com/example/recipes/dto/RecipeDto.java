package com.example.recipes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeDto {
    private String title;
    private String description;
    private String instruction;
    private Set<IngredientDto> ingredients;

    public String getTitle() {
        return title;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<IngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<IngredientDto> ingredients) {
        this.ingredients = ingredients;
    }
}