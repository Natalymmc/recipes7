package com.example.recipes.repository;

import com.example.recipes.entity.Recipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE "
            + "LOWER(i.name) IN :ingredientNames GROUP BY r HAVING COUNT(i) = :ingredientCount")
    List<Recipe> findRecipesByIngredientNames(
            @Param("ingredientNames") List<String> ingredientNames,
            @Param("ingredientCount") long ingredientCount);


    @Query(value = "SELECT r.* FROM recipes r "
            + "JOIN reviews rev ON r.id = rev.recipe_id "
            + "GROUP BY r.id "
            + "HAVING AVG(rev.rating) >= :rating",
        nativeQuery = true)
    List<Recipe> findRecipesByAverageRating(@Param("rating") Double rating);
}
