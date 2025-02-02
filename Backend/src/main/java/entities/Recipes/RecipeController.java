package entities.Recipes;

import entities.Ingredients.*;
import entities.Notifications.NotificationServer;
import entities.Users.User;
import entities.Users.UserController;
import entities.Users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private final String successMessage = "{\"message\":\"success\"}";
    private final String failureMessageTemplate = "{\"message\":\"failure\", \"reason\":\"%s\"}";

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Create a new recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid recipe data",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping
    public ResponseEntity<String> createRecipe(@RequestBody Recipe recipe) {
        if (recipe == null) {
            return new ResponseEntity<>(String.format(failureMessageTemplate, "Recipe cannot be null"), HttpStatus.BAD_REQUEST);
        }
        if (recipeRepository.findByRid(recipe.getRid()) != null) {
            return new ResponseEntity<>(String.format(failureMessageTemplate, "Recipe with this ID already exists"), HttpStatus.BAD_REQUEST);
        }

        for (String ingredientName : recipe.getIngredientList()) {
            // Check if the ingredient already exists by name
            if (ingredientRepository.findByIname(ingredientName) == null) {
                Ingredient ingredient = new Ingredient();
                ingredient.setIname(ingredientName);
                ingredientRepository.save(ingredient);
                RecipeService.addOpenFoodFactsData(ingredient);
            }
        }

        User user = (userController.getUserByUid(recipe.getUid())).getBody();

        if (user == null) {
            return new ResponseEntity<>("Invalid rating data, user doesnt exist", HttpStatus.BAD_REQUEST);
        }
        assert user != null;
        user.setRecipesPosted(user.getRecipesPosted() + 1);
        userRepository.save(user);

        NotificationServer.sendNewRecipeNotification(recipe.getRname()); // Websocket call
        recipeRepository.save(recipe);
        String successMessageRid = "{\"message\":\"success\",\n\"rid\": " + recipe.getRid() + "}";
        return new ResponseEntity<>(successMessageRid, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PutMapping
    public ResponseEntity<String> updateRecipe(@RequestBody Recipe recipe) {
        Recipe existingRecipe = recipeRepository.findByRid(recipe.getRid());
        if (existingRecipe == null) {
            return new ResponseEntity<>("Recipe not found", HttpStatus.NOT_FOUND);
        }

        existingRecipe.setRname(recipe.getRname());
        existingRecipe.setUid(recipe.getUid());
        existingRecipe.setServings(recipe.getServings());
        existingRecipe.setDietType(recipe.getDietType());
        existingRecipe.setCaloriesPerServing(recipe.getCaloriesPerServing());
        existingRecipe.setPrepTime(recipe.getPrepTime());
        existingRecipe.setAmountsList(recipe.getAmountsList());
        existingRecipe.setIngredientList(recipe.getIngredientList());
        existingRecipe.setDirections(recipe.getDirections());
        existingRecipe.setCookTime(recipe.getCookTime());
        existingRecipe.setTotalTime(recipe.getTotalTime());
        existingRecipe.setAverageRating(recipe.getAverageRating());
        existingRecipe.setTotalRatings(recipe.getTotalRatings());

        // If a new image is provided, associate it with the recipe
        if (recipe.getImage() != null) {
            existingRecipe.setImage(recipe.getImage());  // Update image association
        }

        recipeRepository.save(existingRecipe);

        return new ResponseEntity<>("Recipe updated successfully", HttpStatus.OK);
    }

    @Operation(summary = "Partially update a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PatchMapping
    public ResponseEntity<String> updatePartialRecipe(@RequestBody Recipe recipe) {
        Recipe existingRecipe = recipeRepository.findByRid(recipe.getRid());
        if (existingRecipe == null) {
            return new ResponseEntity<>("Recipe not found", HttpStatus.NOT_FOUND);
        }

        // Update only non-null fields
        if (recipe.getRname() != null) {
            existingRecipe.setRname(recipe.getRname());
        }

        if (recipe.getUid() > 0) {
            existingRecipe.setUid(recipe.getUid());
        }

        if (recipe.getServings() > 0) {
            existingRecipe.setServings(recipe.getServings());
        }

        if (recipe.getDietType() != null) {
            existingRecipe.setDietType(recipe.getDietType());
        }
        if (recipe.getCaloriesPerServing() != 0) {
            existingRecipe.setCaloriesPerServing(recipe.getCaloriesPerServing());
        }
        if (recipe.getPrepTime() > 0) {
            existingRecipe.setPrepTime(recipe.getPrepTime());
        }

        if (recipe.getCookTime() > 0) {
            existingRecipe.setCookTime(recipe.getCookTime());
        }

        if (recipe.getTotalTime() > 0) {
            existingRecipe.setTotalTime(recipe.getTotalTime());
        }

        if (recipe.getDirections() != null) {
            existingRecipe.setDirections(recipe.getDirections());
        }

        if (recipe.getIngredientList() != null) {
            existingRecipe.setIngredientList(recipe.getIngredientList());
        }

        if (recipe.getAmountsList() != null) {
            existingRecipe.setAmountsList(recipe.getAmountsList());
        }

        if (recipe.getImage() != null) {
            existingRecipe.setImage(recipe.getImage());
        }

        if (recipe.getAverageRating() > 0.0) {
            existingRecipe.setAverageRating(recipe.getAverageRating());
        }

        recipeRepository.save(existingRecipe);

        return new ResponseEntity<>("Recipe updated successfully", HttpStatus.OK);
    }

    @Operation(summary = "Partially update a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PatchMapping("/rating")
    public ResponseEntity<String> updateRecipeRating(@RequestBody Recipe recipe) {
        Recipe existingRecipe = recipeRepository.findByRid(recipe.getRid());
        if (existingRecipe == null) {
            return new ResponseEntity<>("Recipe not found", HttpStatus.NOT_FOUND);
        }

        if (recipe.getAverageRating() > 0.0) {
            existingRecipe.setAverageRating(recipe.getAverageRating());
        }
        if (recipe.getTotalRatings() > 0) {
            existingRecipe.setTotalRatings(recipe.getTotalRatings());
        }

        recipeRepository.save(existingRecipe);
        return new ResponseEntity<>("Recipe updated successfully", HttpStatus.OK);
    }

    @Operation(summary = "Filter recipes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered recipes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recipes found")
    })
    @GetMapping("/filter")
    public ResponseEntity<List<Recipe>> searchAndFilterRecipes(
            @RequestParam(value = "partialName", required = false) String partialName,
            @RequestParam(value = "dietType", required = false) String dietType,
            @RequestParam(value = "uid", required = false) Integer uid,
            @RequestParam(value = "maxCalories", required = false) Integer maxCalories,
            @RequestParam(value = "maxCookTime", required = false) Integer maxCookTime,
            @RequestParam(value = "maxPrepTime", required = false) Integer maxPrepTime,
            @RequestParam(value = "minServings", required = false) Integer minServings,
            @RequestParam(value = "maxServings", required = false) Integer maxServings,
            @RequestParam(value = "minCalories", required = false) Integer minCalories,
            @RequestParam(value = "averageRating", required = false) Double averageRating){

        List<Recipe> recipes = recipeRepository.findByFilters(dietType,uid, maxCalories, maxCookTime, maxPrepTime, minServings, maxServings, minCalories, averageRating);

        if (partialName != null && !partialName.isEmpty()) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getRname().toLowerCase().contains(partialName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (recipes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(recipes);
    }

    @Operation(summary = "Get all recipes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recipes found")
    })
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Operation(summary = "Get all ingredients for recipe by rid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No ingredients found")
    })
    @GetMapping("/ingredientsfor/{rid}")
    public ResponseEntity<List<String>> getIngredientsByRecipeId(@PathVariable int rid) {
        Recipe recipe = recipeRepository.findByRid(rid);

        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(recipe.getIngredientList(), HttpStatus.OK);
    }

    @Operation(summary = "Get all directions/steps for recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Directions/Steps retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No directions/steps found")
    })
    @GetMapping("/directionsfor/{rid}")
    public ResponseEntity<List<String>> getStepsByRecipeId(@PathVariable int rid) {
        Recipe recipe = recipeRepository.findByRid(rid);

        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(recipe.getDirections(), HttpStatus.OK);
    }

    @Operation(summary = "Get a single recipe by rid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recipe found")
    })
    @GetMapping("/{rid}")
    public ResponseEntity<Recipe> getRecipeByRid(@PathVariable int rid) {
        Recipe recipe = recipeRepository.findByRid(rid);
        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @Operation(summary = "Get a all recipes posted by a user by uid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recipes found")
    })
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<Recipe>> getRecipesByUid(@PathVariable int uid) {
        List<Recipe> recipes = recipeRepository.findByUid(uid);
        if (recipes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @Operation(summary = "Delete recipe by rid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No recipe found")
    })
    @DeleteMapping("/{rid}")
    public ResponseEntity<String> deleteRecipe(@PathVariable int rid) {
        Recipe existingRecipe = recipeRepository.findByRid(rid);
        if (existingRecipe == null) {
            return new ResponseEntity<>(String.format(failureMessageTemplate, "Recipe not found"), HttpStatus.NOT_FOUND);
        }

        recipeRepository.delete(existingRecipe);
        return new ResponseEntity<>(String.format(successMessage, "Recipe deleted successfully"), HttpStatus.OK);
    }
}