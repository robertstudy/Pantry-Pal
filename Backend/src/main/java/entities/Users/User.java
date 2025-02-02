package entities.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.util.List;
import java.util.ArrayList;

@Entity
@Schema(description = "Represents a user in the application, including personal information and preferences.")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the user", example = "1")
    private int uid;  // User ID

    @Schema(description = "Unique username chosen by the user", example = "johndoe123")
    private String username; // Unique identifier

    @Schema(description = "Password for user authentication", example = "securepassword123")
    private String password;

    @ArraySchema(
            schema = @Schema(description = "Geographical location of the user represented as a list of doubles (latitude, longitude)",
                    example = "[41.40338, 2.17403]"),
            minItems = 2,
            maxItems = 2
    )
    private List<Double> geoLocation; // List of doubles representing geolocation

    @ArraySchema(
            schema = @Schema(description = "List of favorite recipe names", example = "[\"Pasta Carbonara\", \"Chicken Curry\"]")
    )
    private List<String> favoriteRecipes; // List of favorite recipe names

    @Schema(description = "Indicates if the user account is active", example = "true")
    private boolean isActive; // User active status

    @Schema(description = "Shows the amount of recipes a user has posted", example = "5")
    private int recipesPosted; // User active status

    // Constructor with username and password only (empty lists for other fields)

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.geoLocation = new ArrayList<>(); // Initialize as empty list
        this.favoriteRecipes = new ArrayList<>(); // Initialize as empty list
        this.isActive = true; // Default to active
        this.recipesPosted = 0;
    }

    // Default constructor
    public User() {
    }

    // Getters and Setters
    @Schema(description = "Gets the username of the user")
    public String getUsername() {
        return username;
    }

    @Schema(description = "Gets the unique ID of the user")
    public int getuid() {
        return uid;
    }

    @Schema(description = "Sets the username of the user")
    public void setUsername(String username) {
        this.username = username;
    }

    @Schema(description = "Gets the password of the user")
    public String getPassword() {
        return password;
    }

    @Schema(description = "Sets the password of the user")
    public void setPassword(String password) {
        this.password = password;
    }

    @Schema(description = "Gets the geographical location of the user")
    public List<Double> getGeoLocation() {
        return geoLocation;
    }

    @Schema(description = "Sets the geographical location of the user")
    public void setGeoLocation(List<Double> geoLocation) {
        this.geoLocation = geoLocation;
    }

    @Schema(description = "Gets the list of favorite recipes of the user")
    public List<String> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    @Schema(description = "Sets the list of favorite recipes of the user")
    public void setFavoriteRecipes(List<String> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    @Schema(description = "Gets the active status of the user")
    public boolean getIsActive() {
        return isActive;
    }

    @Schema(description = "Sets the active status of the user")
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Schema(description = "Gets the active status of the user")
    public int getRecipesPosted() {
        return recipesPosted;
    }

    @Schema(description = "Sets the active status of the user")
    public void setRecipesPosted(int recipesPosted) {
        this.recipesPosted = recipesPosted;
    }
}
