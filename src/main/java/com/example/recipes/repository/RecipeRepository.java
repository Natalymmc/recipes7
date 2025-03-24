package com.example.recipes.repository;

import com.example.recipes.entity.Recipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    //List<Recipe> findByTitle(String title);
    List<Recipe> findByTitleContainingIgnoreCase(String title);
}
