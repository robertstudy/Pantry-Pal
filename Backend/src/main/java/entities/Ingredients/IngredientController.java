package entities.Ingredients;

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

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientRepository ingredientRepository;

    private final String successMessage = "{\"message\":\"success\"}";
    private final String failureMessageTemplate = "{\"message\":\"failure\", \"reason\":\"%s\"}";

    @Operation(summary = "Create a new ingredient")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ingredient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ingredient data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> createIngredient(@RequestBody Ingredient ingredient) {
        if (ingredient == null || ingredient.getIname() == null || ingredient.getIname().trim().isEmpty()) {
            return new ResponseEntity<>(String.format(failureMessageTemplate, "Ingredient name cannot be null or empty"), HttpStatus.BAD_REQUEST);
        }

        if (ingredientRepository.findByIid(ingredient.getIid()) != null) {
            return new ResponseEntity<>(String.format(failureMessageTemplate, "Ingredient with this ID already exists"), HttpStatus.BAD_REQUEST);
        }
        ingredientRepository.save(ingredient);
        return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
    }

    @Operation(summary = "Retrieve all ingredients")
    @ApiResponse(responseCode = "200", description = "List of ingredients retrieved successfully")
    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Operation(summary = "Retrieve an ingredient by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingredient retrieved successfully", content = @Content(schema = @Schema(implementation = Ingredient.class))),
            @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @GetMapping("/{iid}")
    public ResponseEntity<Ingredient> getIngredientByIid(@PathVariable int iid) {
        Ingredient ingredient = ingredientRepository.findByIid(iid);
        if (ingredient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ingredient, HttpStatus.OK);
    }

    @Operation(summary = "Delete an ingredient by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingredient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @DeleteMapping("/{iid}")
    public ResponseEntity<String> deleteIngredient(@PathVariable int iid) {
        Ingredient existingIngredient = ingredientRepository.findByIid(iid);
        if (existingIngredient == null) {
            return new ResponseEntity<>(String.format(failureMessageTemplate, "Ingredient not found"), HttpStatus.NOT_FOUND);
        }

        ingredientRepository.delete(existingIngredient);
        return new ResponseEntity<>(String.format(successMessage, "Ingredient deleted successfully"), HttpStatus.OK);
    }

    @Operation(summary = "Update an ingredient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @PutMapping
    public ResponseEntity<String> updateIngredient(@RequestBody Ingredient ingredient) {
        Ingredient existingIngredient = ingredientRepository.findByIname(ingredient.getIname());
        if (existingIngredient == null) {
            return new ResponseEntity<>("Ingredient not found", HttpStatus.NOT_FOUND);
        }

        existingIngredient.setIname(ingredient.getIname());
        existingIngredient.setOffid(ingredient.getOffid());
        ingredientRepository.save(existingIngredient);

        return new ResponseEntity<>("Ingredient updated successfully", HttpStatus.OK);
    }

    @Operation(summary = "Partially update an ingredient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found", content = @Content)
    })
    @PatchMapping
    public ResponseEntity<String> updatePartialIngredient(@RequestBody Ingredient ingredient) {
        Ingredient existingIngredient = ingredientRepository.findByIname(ingredient.getIname());
        if (existingIngredient == null) {
            return new ResponseEntity<>("Ingredient not found", HttpStatus.NOT_FOUND);
        }

        if (ingredient.getIname() != null) {
            existingIngredient.setIname(ingredient.getIname());
        }
        if (ingredient.getOffid() != null) {
            existingIngredient.setOffid(ingredient.getOffid());
        }

        ingredientRepository.save(existingIngredient);

        return new ResponseEntity<>("Ingredient updated successfully", HttpStatus.OK);
    }
}