package entities.Recipes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import entities.Images.Image;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the recipe", example = "1")
    private int rid;

    @Schema(description = "Name of the recipe", example = "Spaghetti Bolognese")
    private String rname;

    @Schema(description = "ID of the user who created the recipe", example = "101")
    private int uid;

    @Schema(description = "Number of servings", example = "4")
    private int servings;

    @Schema(description = "Diet type of the recipe (e.g., Vegetarian, Vegan, etc.)", example = "Vegetarian")
    private String dietType;

    @Schema(description = "Calories per serving", example = "250")
    private int caloriesPerServing;

    @Schema(description = "Preparation time in minutes", example = "15")
    private Integer prepTime;

    @Schema(description = "Cooking time in minutes", example = "30")
    private Integer cookTime;

    @Schema(description = "Total time required for the recipe in minutes", example = "45")
    private Integer totalTime;

    @ElementCollection
    @Schema(description = "List of ingredients used in the recipe", example = "[\"flour\", \"salt\"]")
    private List<String> ingredientList = new ArrayList<>();

    @ElementCollection
    @Schema(description = "List of ingredients used in the recipe", example = "[\"2 cups\", \"1/2 tsp\"]")
    private List<String> amountsList = new ArrayList<>();

    @ElementCollection
    @Schema(description = "List of cooking directions", example = "[\"Mix ingredients\", \"Bake for 30 minutes\"]")
    private List<String> directions = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    @Schema(description = "Image associated with the recipe")
    private Image image;

    @Schema(description = "Average rating for recipe across all review/ratings", example = "4.5")
    private Double averageRating;

    @Schema(description = "Amount of ratings given by users for that recipe", example = "153")
    private Integer totalRatings;


    public Recipe() {}

    public Recipe(String name, int uid, int servings, String dietType, List<String> ingredientList, List<String> amountsList,int caloriesPerServing, List<String> directions, Integer prepTime, Integer cookTime, Integer totalTime, Image image, double averageRating, int totalRatings) {
        this.rname = name;
        this.uid = uid;
        this.servings = servings;
        this.dietType = dietType;
        this.ingredientList = ingredientList;
        this.caloriesPerServing = caloriesPerServing;
        this.directions = directions;
        this.amountsList = amountsList;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.totalTime = totalTime;
        this.image = image;
        this.averageRating = 0.0;
        this.totalRatings = 0;
    }

    /**
     * Constructor for creating a Recipe with a name.
     * @param name The name of the recipe.
     */

    public Recipe(String name) {
        this.rname = name;
    }

    /**
     * Gets the recipe ID.
     * @return The unique identifier for the recipe.
     */
    @Schema(description = "Gets the unique identifier for the recipe", example = "1")
    public int getRid() {
        return rid;
    }

    /**
     * Sets the recipe ID.
     * @param rid The unique identifier for the recipe.
     */
    @Schema(description = "Sets the unique identifier for the recipe", example = "1")
    public void setRid(
            @Schema(description = "The unique identifier for the recipe", example = "1")
            int rid) {
        this.rid = rid;
    }

    /**
     * Gets the user ID associated with the recipe.
     * @return The user ID.
     */
    @Schema(description = "Gets the user ID associated with the recipe", example = "101")
    public int getUid() {
        return uid;
    }

    /**
     * Sets the user ID associated with the recipe.
     * @param uid The user ID.
     */
    @Schema(description = "Sets the user ID associated with the recipe", example = "101")
    public void setUid(
            @Schema(description = "The user ID associated with the recipe", example = "101")
            int uid) {
        this.uid = uid;
    }

    /**
     * Gets the recipe name.
     * @return The name of the recipe.
     */
    @Schema(description = "Gets the name of the recipe", example = "Spaghetti Bolognese")
    public String getRname() {
        return rname;
    }

    /**
     * Sets the recipe name.
     * @param rname The name of the recipe.
     */
    @Schema(description = "Sets the name of the recipe", example = "Spaghetti Bolognese")
    public void setRname(
            @Schema(description = "The name of the recipe", example = "Spaghetti Bolognese")
            String rname) {
        this.rname = rname;
    }

    /**
     * Gets the number of servings.
     * @return The number of servings for the recipe.
     */
    @Schema(description = "Gets the number of servings for the recipe", example = "4")
    public int getServings() {
        return servings;
    }

    /**
     * Sets the number of servings.
     * @param servings The number of servings for the recipe.
     */
    @Schema(description = "Sets the number of servings for the recipe", example = "4")
    public void setServings(
            @Schema(description = "The number of servings for the recipe", example = "4")
            int servings) {
        this.servings = servings;
    }

    /**
     * Gets the diet type of the recipe.
     * @return The diet type (e.g., Vegetarian, Vegan).
     */
    @Schema(description = "Gets the diet type of the recipe", example = "Vegetarian")
    public String getDietType() {
        return dietType;
    }

    /**
     * Sets the diet type of the recipe.
     * @param dietType The diet type (e.g., Vegetarian, Vegan).
     */
    @Schema(description = "Sets the diet type of the recipe", example = "Vegetarian")
    public void setDietType(
            @Schema(description = "The diet type (e.g., Vegetarian, Vegan)", example = "Vegetarian")
            String dietType) {
        this.dietType = dietType;
    }

    /**
     * Gets the list of ingredients.
     * @return A list of ingredients used in the recipe.
     */
    @Schema(description = "Gets the list of ingredients used in the recipe", example = "[\"2 cups of flour\", \"1/2 tsp salt\"]")
    public List<String> getIngredientList() {
        return ingredientList;
    }

    /**
     * Sets the list of ingredients.
     * @param ingredientList A list of ingredients used in the recipe.
     */
    @Schema(description = "Sets the list of ingredients used in the recipe", example = "[\"2 cups of flour\", \"1/2 tsp salt\"]")
    public void setIngredientList(
            @Schema(description = "A list of ingredients used in the recipe", example = "[\"2 cups of flour\", \"1/2 tsp salt\"]")
            List<String> ingredientList) {
        this.ingredientList = ingredientList;
    }


    /**
     * Gets the list of ingredient amounts (amount and unit)
     * @return A list of ingredients amounts used in the recipe.
     */
    @Schema(description = "Gets the list of ingredient amounts used in the recipe", example = "[\"flour\", \"salt\"]")
    public List<String> getAmountsList() {
        return amountsList;
    }

    /**
     * Sets the list of ingredient amounts.
     * @param amountsList A list of ingredient amounts used in the recipe.
     */
    @Schema(description = "Sets the list of ingredient amounts used in the recipe", example = "[\"2 cups\", \"1/2 tsp\"]")
    public void setAmountsList(
            @Schema(description = "A list of ingredients used in the recipe", example = "[\"2 cups\", \"1/2 tsp\"]")
            List<String> amountsList) {
        this.amountsList = amountsList;
    }

    /**
     * Gets the calories per serving.
     * @return The number of calories per serving.
     */
    @Schema(description = "Gets the number of calories per serving", example = "250")
    public int getCaloriesPerServing() {
        return caloriesPerServing;
    }

    /**
     * Sets the calories per serving.
     * @param caloriesPerServing The number of calories per serving.
     */
    @Schema(description = "Sets the number of calories per serving", example = "250")
    public void setCaloriesPerServing(
            @Schema(description = "The number of calories per serving", example = "250")
            int caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }

    /**
     * Gets the cooking directions.
     * @return A list of directions for preparing the recipe.
     */
    @Schema(description = "Gets the list of cooking directions", example = "[\"Mix ingredients\", \"Bake for 30 minutes\"]")
    public List<String> getDirections() {
        return directions;
    }

    /**
     * Sets the cooking directions.
     * @param directions A list of directions for preparing the recipe.
     */
    @Schema(description = "Sets the list of cooking directions", example = "[\"Mix ingredients\", \"Bake for 30 minutes\"]")
    public void setDirections(
            @Schema(description = "A list of directions for preparing the recipe", example = "[\"Mix ingredients\", \"Bake for 30 minutes\"]")
            List<String> directions) {
        this.directions = directions;
    }



    /**
     * Gets the preparation time.
     * @return The preparation time in minutes.
     */
    @Schema(description = "Gets the preparation time in minutes", example = "15")
    public Integer getPrepTime() {
        return prepTime;
    }

    /**
     * Sets the preparation time.
     * @param prepTime The preparation time in minutes.
     */
    @Schema(description = "Sets the preparation time in minutes", example = "15")
    public void setPrepTime(
            @Schema(description = "The preparation time in minutes", example = "15")
            Integer prepTime) {
        this.prepTime = prepTime;
    }

    /**
     * Gets the cooking time.
     * @return The cooking time in minutes.
     */
    @Schema(description = "Gets the cooking time in minutes", example = "30")
    public Integer getCookTime() {
        return cookTime;
    }

    /**
     * Sets the cooking time.
     * @param cookTime The cooking time in minutes.
     */
    @Schema(description = "Sets the cooking time in minutes", example = "30")
    public void setCookTime(
            @Schema(description = "The cooking time in minutes", example = "30")
            Integer cookTime) {
        this.cookTime = cookTime;
    }

    /**
     * Gets the total time for preparing and cooking the recipe.
     * @return The total time in minutes.
     */
    @Schema(description = "Gets the total time for preparing and cooking the recipe", example = "45")
    public Integer getTotalTime() {
        return totalTime;
    }

    /**
     * Sets the total time for preparing and cooking the recipe.
     * @param totalTime The total time in minutes.
     */
    @Schema(description = "Sets the total time for preparing and cooking the recipe", example = "45")
    public void setTotalTime(
            @Schema(description = "The total time in minutes", example = "45")
            Integer totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * Gets the image associated with the recipe.
     * @return The image object.
     */
    @Schema(description = "Gets the image associated with the recipe")
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image associated with the recipe.
     * @param image The image object.
     */
    @Schema(description = "Sets the image associated with the recipe")
    public void setImage(
            @Schema(description = "The image object")
            Image image) {
        this.image = image;
    }

    /**
     * @return The average rating across all user ratings.
     */
    @Schema(description = "Gets the total time for preparing and cooking the recipe", example = "45")
    public Double getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating across all user ratings.
     * @param averageRating The average rating across all user ratings.
     */
    @Schema(description = "Sets the average rating across all user ratings.", example = "4.5")
    public void setAverageRating(
            @Schema(description = "The average rating across all user ratings", example = "4.5")
            Double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * @return The total amount of ratings for that recipe.
     */
    @Schema(description = "Gets the total amount of ratings for that recipe", example = "425")
    public Integer getTotalRatings() {
        return totalRatings;
    }

    /**
     * Sets the total amount of ratings for that recipe
     * @param totalRatings The total amount of ratings for that recipe.
     */
    @Schema(description = "Sets the total amount of ratings for that recipe.", example = "323")
    public void setTotalRatings(
            @Schema(description = "The total amount of ratings for that recipe", example = "323")
            Integer totalRatings) {
        this.totalRatings = totalRatings;
    }
}