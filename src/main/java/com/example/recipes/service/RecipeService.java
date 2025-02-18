package com.example.recipes.service;

import com.example.recipes.model.Recipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    private final List<Recipe> recipes = new ArrayList<>();


    // @PostConstruct
    public RecipeService() {
        recipes.add(new Recipe(1L, "Борщ", "Традиционный украинский суп",
                Arrays.asList("Свекла", "Капуста", "Мясо"),
                "1. Нарежьте свеклу и капусту. 2. Варите мясо. 3. Добавьте овощи в бульон."));
        recipes.add(new Recipe(2L, "Паста Карбонара", "Итальянское блюдо с беконом и сыром",
                Arrays.asList("Паста", "Бекон", "Яйца", "Сыр Пармезан"),
                "1. Отварите пасту. 2. Обжарьте бекон. 3. Смешайте яйца и сыр, добавьте в пасту."));
        // Добавьте больше рецептов по желанию
    }

    public Recipe getRecipeById(Long id) {
        return recipes.stream()
                .filter(recipe -> recipe.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Recipe> getRecipesByTitle(String title) {
        return recipes.stream()
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
}
