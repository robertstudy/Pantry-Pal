package entities.Ratings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
@Schema(description = "Represents a rating for a recipe in the application, including comments and a numerical rating.")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for each rating", example = "1")
    private int rateid;  // rating ID

    @Schema(description = "Unique identifier for the user that gave the rating", example = "1")
    private int uid;  // User ID

    @Schema(description = "Unique identifier for the recipe that the rating corresponds to.", example = "1")
    private int rid;  // User ID

    @Schema(description = "String value to hold the username of the user who gave the review", example = "1")
    private String username;

    @Schema(description = "Double value to hold the stars rating assigned by the user for one specific rating.", example = "4.5")
    private double starsRating;

    @Schema(description = "String value to hold the comment given by the users for the recipeUnique identifier for the recipe that the rating corresponds to.", example = "1")
    private String comment;

    // Constructor
    public Rating(int uid, int rid, double starsRating, String comment) {
        this.uid = uid;
        this.rid = rid;
        this.starsRating = starsRating;
        this.comment = comment;
        this.username = "";
    }

    // Default constructor
    public Rating() {
    }

    @Schema(description = "Gets the user id")
    public int getRateid() {
        return rateid;
    }

    @Schema(description = "Sets the user id")
    public void setRateid(int rateid) {
        this.rateid = uid;
    }
    @Schema(description = "Gets the user id")
    public int getUid() {
        return uid;
    }

    @Schema(description = "Sets the user id")
    public void setUid(int uid) {
        this.uid = uid;
    }

    @Schema(description = "Gets the recipe id")
    public int getRid() {
        return rid;
    }

    @Schema(description = "Sets the recipe id")
    public void setRid(int rid) {
        this.rid = rid;
    }

    @Schema(description = "Gets the stars rating for that individual rating")
    public double getStarsRating() {
        return starsRating;
    }

    @Schema(description = "Sets the stars rating")
    public void setStarsRating(double starsRating) {
        this.starsRating = starsRating;
    }

    @Schema(description = "Gets the comment for a specific rating")
    public String getComment() {
        return comment;
    }

    @Schema(description = "Sets the comment given by the user")
    public void setComment(String comment) {
        this.comment = comment;
    }


    @Schema(description = "Gets the username for a specific rating")
    public String getUsername() {
        return username;
    }

    @Schema(description = "Sets the username for a rating")
    public void setUsername(String username) {
        this.username = username;
    }
}
