package entities.Ratings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Favorites.Favorite;
import entities.Recipes.Recipe;
import entities.Recipes.RecipeController;
import entities.Recipes.RecipeRepository;
import entities.Users.User;
import entities.Users.UserController;
import entities.Users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;

    // JSON response messages
    private final String success = "{\"message\":\"success\"}";
    private final String failure = "{\"message\":\"failure\"}";
    @Autowired
    private RecipeController recipeController;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository UserRepository;

    @Operation(summary = "Create a new rating", description = "Creates a new rating in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rating created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating data or username already exists")
    })
    @PostMapping
    public ResponseEntity<String> createRating(@RequestBody Rating rating) {
        if (rating == null || rating.getUid() == 0 || rating.getRid() == 0 ||
                rating.getStarsRating() > 5.0 || rating.getStarsRating() < 0.0) {
            return new ResponseEntity<>("Invalid rating data", HttpStatus.BAD_REQUEST);
        }

        Recipe recipe = recipeController.getRecipeByRid(rating.getRid()).getBody();
        User userBody = userController.getUserByUid(rating.getUid()).getBody();

        if (recipe == null) {
            return new ResponseEntity<>("Invalid rating data, recipe doesnt exist", HttpStatus.BAD_REQUEST);
        }

        if (userBody == null) {
            return new ResponseEntity<>("Invalid rating data, user doesnt exist", HttpStatus.BAD_REQUEST);
        }

        // Check if the user has already rated this recipe
        Rating existingRating = ratingRepository.findByUidAndRid(rating.getUid(), rating.getRid());
        if (existingRating != null) {
            return new ResponseEntity<>("User has already rated this recipe", HttpStatus.CONFLICT);
        }

        User user = UserRepository.findByUid(rating.getUid());
        rating.setUsername(user.getUsername());
        ratingRepository.save(rating);

        Double currentAverageRating = recipe.getAverageRating();
        if (currentAverageRating == null) {
            currentAverageRating = 0.0;
        }

        Integer totalRatings = recipe.getTotalRatings();
        if (totalRatings == null){
            totalRatings = 0;
        }

        double newAverageRating = ((currentAverageRating * totalRatings) + rating.getStarsRating()) / (totalRatings + 1);

        newAverageRating = Math.round(newAverageRating * 10.0) / 10.0;
        recipe.setAverageRating(newAverageRating);
        recipe.setTotalRatings(totalRatings + 1);
        recipeRepository.save(recipe);

        return new ResponseEntity<>("Rating created successfully", HttpStatus.CREATED);
    }

    @Operation(summary = "Get all ratings", description = "Retrieves a list of all ratings.")
    @ApiResponse(responseCode = "200", description = "List of all ratings retrieved successfully")
    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    @Operation(
            summary = "Get user's ratings",
            description = "Fetches all ratings for a specific user by their user ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of user's ratings retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No ratings found for the user.")
    })
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<Rating>> getRatingsByUser(
            @Parameter(description = "The ID of the user whose ratings to retrieve.", example = "4")
            @PathVariable int uid
    ) {
        List<Rating> rating = ratingRepository.findByUid(uid);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/user/avg/{uid}")
    public ResponseEntity<Map<String, Object>> getRatingAvgForUser(@PathVariable int uid) {
        Double avgRating = ratingRepository.findAvgByUid(uid);
        String message = "";
        // Handle case where avgRating is null
        if (avgRating == null) {
            avgRating = 0.0;
            message = "No ratings yet";
        } else {
            avgRating = Math.round(avgRating * 10.0) / 10.0;
            message = "There are ratings for your recipes";
        }

        // Label the response data
        Map<String, Object> response = new HashMap<>();
        response.put("uid", uid);
        response.put("avgRating", avgRating);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get recipe's ratings",
            description = "Fetches all ratings for a specific recipe by its recipe ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of recipe's ratings retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No ratings found for the recipe.")
    })
    @GetMapping("/recipe/{rid}")
    public ResponseEntity<List<Rating>> getRatingsByRecipe(
            @Parameter(description = "The ID of the recipe whose ratings to retrieve.", example = "4")
            @PathVariable int rid
    ) {
        List<Rating> rating = ratingRepository.findByRid(rid);
        return ResponseEntity.ok(rating);
    }

    @Operation(
            summary = "Remove a rating",
            description = "Deletes a specific rating by its unique rating ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rating removed successfully."),
            @ApiResponse(responseCode = "404", description = "Rating not found.")
    })
    @DeleteMapping("/{rateid}")
    public ResponseEntity<Void> removeRating(
            @Parameter(description = "The ID of the rating to delete.", example = "1")
            @PathVariable int rateid
    ) {
        ratingRepository.deleteById(rateid);
        return ResponseEntity.noContent().build();
    }
}