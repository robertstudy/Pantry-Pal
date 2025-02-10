package entities.Favorites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Entity representing a user's favorite recipe.")
@Entity
@Table(name = "favorite")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the favorite entry.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int fid;  // Favorite ID

    @Schema(description = "Unique identifier for the user who favorited the recipe.", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private int uid;  // User ID

    @Schema(description = "Unique identifier for the recipe that is favorited.", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
    private int rid;  // Recipe ID

    public Favorite() {
    }

    public Favorite(
            @Schema(description = "Unique identifier for the user.", example = "4") int uid,
            @Schema(description = "Unique identifier for the recipe.", example = "7") int rid) {
        this.uid = uid;
        this.rid = rid;
    }

    /**
     * Retrieves the unique identifier for the favorite.
     *
     * @return The favorite ID.
     */
    @Schema(description = "Retrieves the unique identifier for the favorite.", example = "1")
    public int getFid() {
        return fid;
    }

    /**
     * Sets the unique identifier for the favorite.
     *
     * @param fid The favorite ID.
     */
    @Schema(description = "Sets the unique identifier for the favorite.", example = "1")
    public void setFid(int fid) {
        this.fid = fid;
    }

    /**
     * Retrieves the unique identifier for the user.
     *
     * @return The user ID.
     */
    @Schema(description = "Retrieves the unique identifier for the user.", example = "4")
    public int getUid() {
        return uid;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param uid The user ID.
     */
    @Schema(description = "Sets the unique identifier for the user.", example = "4")
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * Retrieves the unique identifier for the recipe.
     *
     * @return The recipe ID.
     */
    @Schema(description = "Retrieves the unique identifier for the recipe.", example = "7")
    public int getRid() {
        return rid;
    }

    /**
     * Sets the unique identifier for the recipe.
     *
     * @param rid The recipe ID.
     */
    @Schema(description = "Sets the unique identifier for the recipe.", example = "7")
    public void setRid(int rid) {
        this.rid = rid;
    }
}