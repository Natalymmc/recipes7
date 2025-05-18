package com.example.recipes.service;

import com.example.recipes.config.CacheConfig;
import com.example.recipes.dto.IngredientDto;
import com.example.recipes.dto.RecipeDto;
import com.example.recipes.dto.RecipeFullDto;
import com.example.recipes.entity.Ingredient;
import com.example.recipes.entity.Recipe;
import com.example.recipes.exceptions.NotFoundException;
import com.example.recipes.exceptions.ValidationException;
import com.example.recipes.mapper.RecipeMapper;
import com.example.recipes.repository.IngredientRepository;
import com.example.recipes.repository.RecipeRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

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

    public List<RecipeDto> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(recipeMapper::convertToDto)
                .toList();
    }

    @Transactional
    public RecipeDto getRecipeById(Long id) {
        if (id <= 0) {
            throw new ValidationException("Recipe ID must be greater than 0.");
        }

        String cacheKey = "recipe_" + id;
        if (cacheService.containsKey(cacheKey)) {
            return (RecipeDto) cacheService.get(cacheKey);
        }

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found with ID " + id));

        RecipeDto recipeDto = recipeMapper.convertToDto(recipe);
        cacheService.put(cacheKey, recipeDto);
        return recipeDto;
    }

    public List<RecipeDto> findRecipesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Recipe title cannot be null or empty.");
        }
        String cacheKey = "recipes_by_title_" + title.toLowerCase();
        if (cacheService.containsKey(cacheKey)) {
            return (List<RecipeDto>) cacheService.get(cacheKey);
        }

        List<RecipeDto> recipeDtos = recipeRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(recipeMapper::convertToDto)
                .toList();

        if (recipeDtos.isEmpty()) {
            throw new NotFoundException("No recipes found with title containing: " + title);
        }

        cacheService.put(cacheKey, recipeDtos);
        return recipeDtos;
    }
    //убрать фул
    /*
    @Transactional
    public RecipeDto createRecipe(RecipeDto recipeDto) {
        if (recipeDto.getTitle() == null || recipeDto.getTitle().isEmpty()) {
            throw new ValidationException("Recipe title cannot be null or empty.");
        }

        if (recipeDto.getIngredients() == null || recipeDto.getIngredients().isEmpty()) {
            throw new ValidationException("Recipe must have at least one ingredient.");
        }

        recipeDto.getIngredients().forEach(ingredientDto -> {
            if (ingredientDto.getName() == null || ingredientDto.getName().trim().isEmpty()) {
                throw new ValidationException("Ingredient name cannot be null or empty.");
            }
        });

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
        recipeRepository.save(recipe);

        clearRecipeCache();
        return recipeMapper.convertToDto(recipe);
    }
*/

    @Transactional
    public RecipeDto createRecipe(RecipeDto recipeDto) {
        if (recipeDto.getTitle() == null || recipeDto.getTitle().isEmpty()) {
            throw new ValidationException("Recipe title cannot be null or empty.");
        }

        // Check the length of the recipe title
        if (recipeDto.getTitle().length() > 36) { // Set your desired max length
            throw new ValidationException("Recipe title cannot exceed 36 characters.");
        }

        if (recipeDto.getIngredients() == null || recipeDto.getIngredients().isEmpty()) {
            throw new ValidationException("Recipe must have at least one ingredient.");
        }

        if (recipeDto.getDescription() == null || recipeDto.getDescription().isEmpty()) {
            throw new ValidationException("Recipe description cannot be null or empty.");
        }

        if (recipeDto.getInstruction() == null || recipeDto.getInstruction().isEmpty()) {
            throw new ValidationException("Recipe instruction cannot be null or empty.");
        }

        recipeDto.getIngredients().forEach(ingredientDto -> {
            if (ingredientDto.getName() == null || ingredientDto.getName().trim().isEmpty()) {
                throw new ValidationException("Ingredient name cannot be null or empty.");
            }
        });

        Recipe recipe = new Recipe(
                recipeDto.getTitle(),
                recipeDto.getDescription(),
                recipeDto.getInstruction()
        );

        // Убираем дублирующиеся ингредиенты по имени
        Set<String> uniqueNames = new HashSet<>();
        Set<Ingredient> ingredients = recipeDto.getIngredients().stream()
                .filter(ingredientDto -> uniqueNames.add(ingredientDto.getName()))
                .map(ingredientDto -> ingredientRepository.findByName(ingredientDto.getName())
                        .orElseGet(() -> ingredientRepository
                                .save(new Ingredient(ingredientDto.getName()))))
                .collect(Collectors.toSet());

        recipe.setIngredients(ingredients);
        recipeRepository.save(recipe);

        clearRecipeCache();
        return recipeMapper.convertToDto(recipe);
    }
    /*
    public RecipeDto updateRecipe(Long recipeId, RecipeDto recipeDto) {
        if (recipeId == null || recipeId <= 0) {
            throw new ValidationException("Recipe ID must be greater than 0.");
        }

        // Проверка на пустое название рецепта
        if (recipeDto.getTitle() == null || recipeDto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Recipe title cannot be null or empty.");
        }
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found with ID " + recipeId));
        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setInstruction(recipeDto.getInstruction());

        Recipe updatedRecipe = recipeRepository.save(recipe);

        cacheService.evict("recipe_" + recipeId);
        clearRecipeCache();
        return recipeMapper.convertToDto(updatedRecipe);
    }
*/

    public RecipeDto updateRecipe(Long recipeId, RecipeDto recipeDto) {
        // Валидация входных данных
        if (recipeId == null || recipeId <= 0) {
            throw new ValidationException("Recipe ID must be greater than 0.");
        }
        if (recipeDto.getTitle() == null || recipeDto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Recipe title cannot be null or empty.");
        }

        // Check the length of the recipe title
        if (recipeDto.getTitle().length() > 36) { // Set your desired max length
            throw new ValidationException("Recipe title cannot exceed 36 characters.");
        }

        if (recipeDto.getIngredients() == null || recipeDto.getIngredients().isEmpty()) {
            throw new ValidationException("Recipe must have at least one ingredient.");
        }

        if (recipeDto.getDescription() == null || recipeDto.getDescription().isEmpty()) {
            throw new ValidationException("Recipe description cannot be null or empty.");
        }

        if (recipeDto.getInstruction() == null || recipeDto.getInstruction().isEmpty()) {
            throw new ValidationException("Recipe instruction cannot be null or empty.");
        }

        recipeDto.getIngredients().forEach(ingredientDto -> {
            if (ingredientDto.getName() == null || ingredientDto.getName().trim().isEmpty()) {
                throw new ValidationException("Ingredient name cannot be null or empty.");
            }
        });

        // Получаем рецепт из базы данных
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found with ID " + recipeId));

        // Обновляем основные поля
        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setInstruction(recipeDto.getInstruction());

        // Обрабатываем ингредиенты
        if (recipeDto.getIngredients() != null) {
            // Получаем текущие ингредиенты рецепта
            Set<Ingredient> currentIngredients = recipe.getIngredients();
            if (currentIngredients == null) {
                currentIngredients = new HashSet<>();
            }

            // Создаем новый набор ингредиентов
            Set<Ingredient> updatedIngredients = new HashSet<>();

            // Для каждого ингредиента из DTO
            for (IngredientDto ingredientDto : recipeDto.getIngredients()) {
                // Находим ингредиент в базе
                Ingredient ingredient = ingredientRepository.findById(ingredientDto.getId())
                        .orElseThrow(() -> new NotFoundException(
                                "Ingredient not found with ID " + ingredientDto.getId()));

                updatedIngredients.add(ingredient);
            }

            // Устанавливаем обновленный набор ингредиентов
            recipe.setIngredients(updatedIngredients);
        }

        // Сохраняем изменения
        Recipe updatedRecipe = recipeRepository.save(recipe);

        // Очищаем кэш
        cacheService.evict("recipe_" + recipeId);
        clearRecipeCache();

        return recipeMapper.convertToDto(updatedRecipe);
    }

    @Transactional
    public void deleteRecipeById(Long id) {
        if (id <= 0) {
            throw new ValidationException("Recipe ID must be greater than 0.");
        }
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found with ID " + id));

        recipe.getIngredients().clear();
        recipeRepository.save(recipe);

        recipeRepository.delete(recipe);

        cacheService.evict("recipe_" + id);
        clearRecipeCache();
    }

    public List<RecipeDto> findRecipesByIngredientNames(List<String> ingredientNames) {
        if (ingredientNames == null || ingredientNames.isEmpty()) {
            throw new ValidationException("Ingredient list cannot be null or empty.");
        }

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

        if (recipeDtos.isEmpty()) {
            throw new NotFoundException("No recipes found "
                    + "with ingredients: " + ingredientNames);
        }

        cacheService.put(cacheKey, recipeDtos);
        return recipeDtos;
    }

    public List<RecipeDto> findRecipesByAverageRating(String rating) {
        if (rating == null || rating.trim().isEmpty()) {
            throw new ValidationException("Rating cannot be null or empty.");
        }

        double normalizedRating;
        try {
            normalizedRating = Double.parseDouble(rating.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValidationException("Rating must be a valid numeric value.");
        }

        if (normalizedRating < 0 || normalizedRating > 10) {
            throw new ValidationException("Rating must be between 0 and 10.");
        }

        String cacheKey = "recipes_by_rating_" + normalizedRating;
        if (cacheService.containsKey(cacheKey)) {
            return (List<RecipeDto>) cacheService.get(cacheKey);
        }

        List<Recipe> recipes = recipeRepository.findRecipesByAverageRating(normalizedRating);

        if (recipes.isEmpty()) {
            throw new NotFoundException("No recipes found with "
                    + "average rating: " + normalizedRating);
        }

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
