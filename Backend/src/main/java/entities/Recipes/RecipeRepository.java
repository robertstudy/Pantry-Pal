package entities.Recipes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findByRid(int rid);
    Recipe findByRname(String rname);
    List<Recipe> findByRnameContainingIgnoreCase(String partialName);
    List <Recipe> findByUid(int uid);

    @Query("SELECT r FROM Recipe r WHERE " +
            "(:dietType IS NULL OR r.dietType = :dietType) AND " +
            "(:uid IS NULL OR r.uid = :uid) AND " +
            "(:maxCalories IS NULL OR r.caloriesPerServing <= :maxCalories) AND " +
            "(:maxCookTime IS NULL OR r.cookTime <= :maxCookTime) AND " +
            "(:maxPrepTime IS NULL OR r.prepTime <= :maxPrepTime) AND " +
            "(:minServings IS NULL OR r.servings >= :minServings) AND " +
            "(:maxServings IS NULL OR r.servings <= :maxServings) AND " +
            "(:minCalories IS NULL OR r.caloriesPerServing >= :minCalories) AND " +
            "(:averageRating IS NULL OR r.averageRating >= :averageRating)")

    List<Recipe> findByFilters(
            @Param("dietType") String dietType,
            @Param("uid") Integer uid,
            @Param("maxCalories") Integer maxCalories,
            @Param("maxCookTime") Integer maxCookTime,
            @Param("maxPrepTime") Integer maxPrepTime,
            @Param("minServings") Integer minServings,
            @Param("maxServings") Integer maxServings,
            @Param("minCalories") Integer minCalories,
            @Param("averageRating") Double averageRating);
}
