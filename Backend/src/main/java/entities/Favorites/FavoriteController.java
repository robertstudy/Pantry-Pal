package entities.Favorites;

import entities.Recipes.*;
import entities.Users.User;
import entities.Users.UserController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@Tag(name = "Favorites Controller", description = "APIs for managing user favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private RecipeController recipeController;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserController userController;

    /**
     * Adds a new favorite.
     *
     * @param favorite The favorite to be added.
     * @return A success message or conflict status.
     */
    @Operation(
            summary = "Add a favorite",
            description = "Creates a new favorite for a user with a recipe ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Favorite object to be added",
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorite added successfully."),
            @ApiResponse(responseCode = "409", description = "This recipe is already in your favorites.")
    })
    @PostMapping
    public ResponseEntity<String> addFavorite(@RequestBody Favorite favorite) {

        if (favoriteRepository.existsByUidAndRid(favorite.getUid(), favorite.getRid())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This recipe is already in your favorites.");
        }

        Recipe recipe = recipeController.getRecipeByRid(favorite.getRid()).getBody();
        User user = userController.getUserByUid(favorite.getUid()).getBody();

        if (recipe == null) {
            return new ResponseEntity<>("Invalid rating data, recipe doesnt exist", HttpStatus.BAD_REQUEST);
        }

        if (user == null) {
            return new ResponseEntity<>("Invalid rating data, user doesnt exist", HttpStatus.BAD_REQUEST);
        }
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return ResponseEntity.ok("Favorite added successfully: " + savedFavorite);
    }

    /**
     * Retrieves all favorites for a user by user ID.
     *
     * @param uid The user ID.
     * @return A list of favorites for the user.
     */
    @Operation(
            summary = "Get user's favorites",
            description = "Fetches all favorite recipes for a specific user by their user ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of user's favorite recipes retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No favorites found for the user.")
    })
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<Favorite>> getFavoriteByUser(
            @Parameter(description = "The ID of the user whose favorites to retrieve.", example = "4")
            @PathVariable int uid
    ) {
        List<Favorite> favorite = favoriteRepository.findByUid(uid);
        return ResponseEntity.ok(favorite);
    }

    /**
     * Removes a favorite by its favorite ID.
     *
     * @param fid The favorite ID to remove.
     * @return A no-content response if successful.
     */
    @Operation(
            summary = "Remove a favorite",
            description = "Deletes a specific favorite by its unique favorite ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Favorite removed successfully."),
            @ApiResponse(responseCode = "404", description = "Favorite not found.")
    })
    @DeleteMapping("/{fid}")
    public ResponseEntity<Void> removeFavorite(
            @Parameter(description = "The ID of the favorite to delete.", example = "1")
            @PathVariable int fid
    ) {
        favoriteRepository.deleteById(fid);
        return ResponseEntity.noContent().build();
    }
}
