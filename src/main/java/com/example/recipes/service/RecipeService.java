package com.example.recipes.service;

import com.example.recipes.config.CacheConfig;
import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.RecipeFullDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.mapper.RecipeMapper;
import com.example.recipes.repository.IngredientRepository;
import com.example.recipes.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

    private static final String RECIPE_NOT_FOUND = "Recipe not found";

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;
    private final CacheConfig cacheService;
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    public RecipeService(RecipeRepository recipeRepository,
                         IngredientRepository ingredientRepository,
                         RecipeMapper recipeMapper,
                         CacheConfig cacheService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeMapper = recipeMapper;
        this.cacheService = cacheService;
    }

    public List<RecipeFullDto> getAllRecipes() {
        String cacheKey = "all_recipes";

        if (cacheService.containsKey(cacheKey)) {
            logger.info("Cache hit for key: {}", cacheKey);
            return (List<RecipeFullDto>) cacheService.get(cacheKey);
        }
        logger.info("Cache miss for key: {}", cacheKey);
        List<Recipe> recipes = recipeRepository.findAll();
        List<RecipeFullDto> recipeDtos = recipes.stream()
                .map(recipeMapper::convertToFullDto)
                .toList();

        cacheService.put(cacheKey, recipeDtos);
        // logger.info("Data cached for key: {}", cacheKey);
        return recipeDtos;
    }

    @Transactional
    public RecipeDto getRecipeById(Long id) {
        String cacheKey = "recipe_" + id;
        if (cacheService.containsKey(cacheKey)) {
            return (RecipeDto) cacheService.get(cacheKey);
        }

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RECIPE_NOT_FOUND));

        RecipeDto recipeDto = recipeMapper.convertToDto(recipe);
        cacheService.put(cacheKey, recipeDto);
        return recipeDto;
    }

    public List<RecipeDto> findRecipesByTitle(String title) {
        String cacheKey = "recipes_by_title_" + title.toLowerCase();
        if (cacheService.containsKey(cacheKey)) {
            return (List<RecipeDto>) cacheService.get(cacheKey);
        }

        List<RecipeDto> recipeDtos = recipeRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(recipeMapper::convertToDto)
                .toList();

        cacheService.put(cacheKey, recipeDtos);
        return recipeDtos;
    }

    @Transactional
    public RecipeFullDto createRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe(
                recipeDto.getTitle(),
                recipeDto.getDescription(),
                recipeDto.getInstruction()
        );

        Set<Ingredient> ingredients = recipeDto.getIngredients().stream()
                .map(ingredientDto -> ingredientRepository.findByName(ingredientDto.getName())
                        .orElseGet(() ->
                                ingredientRepository.save(new Ingredient(ingredientDto.getName()))))
                .collect(Collectors.toSet());

        recipe.setIngredients(ingredients);

        // Recipe savedRecipe = recipeRepository.save(recipe);
        recipeRepository.save(recipe); //замена предыдущей строки

        clearRecipeCache();
        //return recipeMapper.convertToFullDto(savedRecipe);
        return recipeMapper.convertToFullDto(recipe);
    }

    public RecipeFullDto updateRecipe(Long recipeId, RecipeDto recipeDto) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException(RECIPE_NOT_FOUND));
        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setInstruction(recipeDto.getInstruction());

        Recipe updatedRecipe = recipeRepository.save(recipe);

        cacheService.evict("recipe_" + recipeId);
        clearRecipeCache();
        return recipeMapper.convertToFullDto(updatedRecipe);
    }

    @Transactional
    public void deleteRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RECIPE_NOT_FOUND));

        recipe.getIngredients().clear();
        recipeRepository.save(recipe);

        recipeRepository.delete(recipe);

        cacheService.evict("recipe_" + id);
        clearRecipeCache();
    }

    public List<RecipeDto> findRecipesByIngredientNames(List<String> ingredientNames) {
        String cacheKey = "recipes_by_ingredients_" + ingredientNames.toString();
        if (cacheService.containsKey(cacheKey)) {
            return (List<RecipeDto>) cacheService.get(cacheKey);
        }

        List<String> normalizedIngredientNames = ingredientNames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        long ingredientCount = normalizedIngredientNames.size();
        List<Recipe> recipes = recipeRepository
                .findRecipesByIngredientNames(normalizedIngredientNames, ingredientCount);

        List<RecipeDto> recipeDtos = recipes.stream()
                .map(recipeMapper::convertToDto)
                .toList();

        cacheService.put(cacheKey, recipeDtos);
        return recipeDtos;
    }

    public List<RecipeDto> findRecipesByAverageRating(String rating) {
        String cacheKey = "recipes_by_rating_" + rating;
        if (cacheService.containsKey(cacheKey)) {
            return (List<RecipeDto>) cacheService.get(cacheKey);
        }

        Double normalizedRating = Double.parseDouble(rating.replace(",", "."));
        List<Recipe> recipes = recipeRepository.findRecipesByAverageRating(normalizedRating);

        List<RecipeDto> recipeDtos = recipes.stream()
                .map(recipeMapper::convertToDto)
                .toList();

        cacheService.put(cacheKey, recipeDtos);
        return recipeDtos;
    }

    private void clearRecipeCache() {
        //logger.info("Clearing all recipe-related cache entries...");
        logger.info("Current cache keys before clearing: {}", cacheService.getCachedKeys());
        cacheService.evict("all_recipes"); // Явное удаление ключа для всех рецептов
        cacheService.evictByPattern("recipes_*");
        cacheService.evictByPattern("recipe_*");
        logger.info("Cache cleared for patterns: recipes_* and recipe_*");
    }
}
