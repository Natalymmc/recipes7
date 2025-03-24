package com.example.recipes.repository;

import com.example.recipes.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  //  List<Review> findByRecipeId(Long recipeId);
}
